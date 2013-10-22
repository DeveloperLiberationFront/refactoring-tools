package edu.pdx.cs.multiview.jdt.util;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

/**
 * Builds and returns ASTs for clients.  Limits the total number of ASTs
 * in memory by caching ASTs and evicting least-recently-used ASTs.
 * 
 * @author emerson
 */
public abstract class ASTPool <IndexType>{
	
	protected class Entry{
		public CompilationUnit unit;
		public String date;
	}

	private LRUCache<IndexType, Entry> cache;
	private int maxSize = 3;

	protected ASTPool(int size){
		cache = new LRUCache<IndexType, Entry>(maxSize);
		maxSize = size;
	}
	
	public CompilationUnit getAST(IndexType file){
		
		return getEntry(file).unit;
	}

	private Entry getEntry(IndexType file) {
		Entry e = cache.get(file);
		if(e==null){
			fillCache(file);
			e = cache.get(file);
		}
		return e;
	}

	private void fillCache(IndexType file) {
		Entry e = new Entry();
		parse(file,e);
		cache.put(file, e);
	}
	
	public String getDate(IndexType file) {
		
		return getEntry(file).date;
	}

	public PackageDeclaration getPackage(IndexType file) {
		return getAST(file).getPackage();
	}
	
	public void release(IndexType file) {
		cache.remove(file);
	}
	
	protected abstract void parse(IndexType file, Entry e);
	
	private static ASTParser parser;
	
	protected ASTParser getParser() {

		if(parser==null){
			parser = ASTParser.newParser(AST.JLS3);
		}
		
		return parser;
	}

	private static  ASTPool<IFile> fileDefaultPool;
	private static	ASTPool<ICompilationUnit> cuDefaultPool;
	
	public static ASTPool<IFile> getDefault(){
		if(fileDefaultPool==null){
			fileDefaultPool = new ASTFilePool(3);
		}
		return fileDefaultPool;
	}
	
	public static ASTPool<ICompilationUnit> getDefaultCU(){
		if(cuDefaultPool==null){
			cuDefaultPool = new ASTCUPool(3);
		}
		return cuDefaultPool;
	}
}

class ASTFilePool extends ASTPool<IFile>{

	public ASTFilePool(int size) {super(size);}

	protected void parse(IFile file, Entry e){
			
			try {
				String s = JDTUtils.getContents(file.getContents());
				getParser().setSource(s.toCharArray());
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
			
			e.unit = (CompilationUnit)getParser().createAST(null);
		}
}

class ASTCUPool extends ASTPool<ICompilationUnit>{

	public ASTCUPool(int size) {
		super(size);
	}

	@Override
	protected void parse(ICompilationUnit source, Entry e) {
		
		getParser().setSource(source);
		getParser().setResolveBindings(true);
		
		e.unit = (CompilationUnit)getParser().createAST(null);
	}
}


public class Compiler {
	public static final String ProgramName = "cbc";
	public static final String Version = "1.0.0";

	private final ErrorHandler errorHandler;

	public static void main(String[] args) {
		new compiler(ProgramName).commandMain(args);
	}

	public Compiler(String programName) {
		this.errorHandler = new ErrorHandler(programName);
	}

	public void commandMain(String[] args) {
		Options opts = Options.parse(args);
		List<SourceFile> srcs = opts.sourceFiles();
		build(srcs, opts);
	}

	public void build(List<SourceFile> srcs, Options opts) 
		throws CompileException {
		for (SourceFile src : srcs) {
			compile(src.path(), opts.asmFileNameOf(src), opts);
			assemple(src.path(), opts.objFileNameOf(src), opts);
		}
		link(opts);
	}

	public void compile(String srcPath, String destPath, Options opts) 
		throws CompileException {
		AST ast = parserFile(srcPath, opts);
		TypeTable types = opts.typeTable();
		AST sem = semanticAnalyze(ast, types, opts);
		IR ir = new IRGenerator(errorHandler).generate(sem, types);
		String asm = generateAssembly(ir, opts);
		writeFile(destPath, asm);
	}
}

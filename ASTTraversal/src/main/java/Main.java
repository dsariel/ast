package groovy.ast.producer;

import java.util.List;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.classgen.*;
import java.security.CodeSource;

import groovy.lang.*;


//String scriptText = "def test1 = 1; test1 = 2";

class VariableVisitor extends ClassCodeVisitorSupport {
	String vars = "";
	SourceUnit source;
	public void visitExpressionStatement(final ExpressionStatement statement) {
		Expression expression = statement.getExpression();
		if (expression instanceof BinaryExpression) {
			System.out.println(((BinaryExpression) expression).toString());
			//vars += Str((BinaryExpression expression).leftExpression.name) + ":" + Str((BinaryExpression expression).rightExpression.value);
		}

		super.visitExpressionStatement(statement);
	}
	public void visitReturnStatement(ReturnStatement statement) {
		Expression expression = statement.getExpression();
		if (expression instanceof BinaryExpression) {
			System.out.println(((BinaryExpression) expression).toString());
			//vars += Str((BinaryExpression expression).leftExpression.name) + ":" + Str((BinaryExpression expression).rightExpression.value);
		}
		super.visitReturnStatement(statement);
	}
	protected SourceUnit getSourceUnit() {
		return source;
	}
}

class CustomSourceOperation extends CompilationUnit.PrimaryClassNodeOperation {
	CodeVisitorSupport visitor;
	public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
		//    classNode.visitContents(visitor);
	}
}


class MyClassLoader extends GroovyClassLoader {
	CodeVisitorSupport visitor;
	protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
		CompilationUnit cu = super.createCompilationUnit(config, source);
		// cu.addPhaseOperation(new CustomSourceOperation(visitor), Phases.CLASS_GENERATION);
		return cu;
	}
}


class SecurityCheck extends CodeVisitorSupport {
	public void visitBlockStatement(BlockStatement statement) {
		System.out.println ("NEW BLOCK STATEMENT:");
		System.out.println (statement.getText());

		for (ASTNode child : statement.getStatements()) {

			System.out.println( "CHILD FOUND: ");
			System.out.println( child.getText());
			child.visit(this);
		}
	}
}

public class Main{

	private static void printASTNodeInformation(String description, ASTNode node) {

		System.out.println("--------------------------------------------------------");
		System.out.println( "{0}"+ description);

		if (node == null) {
			System.out.println( "node == null");
		} else {
			System.out.println( "Node.getText()  : {0}" +node.getText());
			System.out.println("Node.toString() : {0}" + node.toString());
			System.out.println( "Node.getClass() : {0}" +node.getClass());
			System.out.println("Node.hashCode() : {0}"+node.hashCode());
		}

		System.out.println( "--------------------------------------------------------");
	}

	public static void main(String[] args){
		//		VariableVisitor visitor =  new VariableVisitor();
		//		MyClassLoader myCL = new MyClassLoader(visitor);
		//		//def script = myCL.parseClass(scriptText);
		//
		//		//assert visitor.vars == ["test1":2, "test2":2, "test3":2]
		//		
		//		System.exit(0);
		try {
			String groovy = new String(Files.readAllBytes(Paths.get("/home/dsariel/projects/cibyl/java/snippet.groovy")));
			//			String str = "node { stage 'Archive build output' }";
			//			str = "TOPOLOGY = env.TOPOLOGY?.trim() ?: \"controller:3,compute:2\"";
			//			System.out.println("Hello, World!");
			//		    str = (new GroovyShell().evaluate(str)).toString();
			//			System.out.println(str);
			//			System.exit(0);
		}
		catch(IOException e) {
			e.printStackTrace();
		}   
		List<ASTNode> ast = null;
		try {
			String contents = new String(Files.readAllBytes(Paths.get("/home/dsariel/projects/cibyl/java/snippet.groovy"))); 
			contents = "@GroovyASTTransformationClass(classes = NotYetImplementedASTTransformation.class)\n"
					+ "TOPOLOGY = env.TOPOLOGY?.trim() ?: \"controller:3,compute:2\"";
			//			contents = "@Retention(RetentionPolicy.SOURCE)\n"
			//					+ "@GroovyASTTransformationClass(classes = NotYetImplementedASTTransformation.class)\n";
			//			contents =  "stage('Provision') {\n"
			//					+ "    dir('infrared') {\n"
			//					+ "        TOPOLOGY = env.TOPOLOGY?.trim() ?: \"controller:3,compute:2\""
			//					+ "    }\n" 
			//					+ " }\n";
			AstBuilder astBuilder = new AstBuilder();
			ast = astBuilder.buildFromString(CompilePhase.CONVERSION, false,	contents);
			SecurityCheck securityCheck = new SecurityCheck();
                        System.exit(0);


			for (ASTNode node : ast) {
				node.visit(securityCheck);
				if (node instanceof BlockStatement) {
					List<Statement> statements = ((BlockStatement) node).getStatements();
					if (!statements.isEmpty()) {
						System.out.println(statements.get(0).toString());
						//BlockStatement closureBlock = (BlockStatement) statements.get(0);
					}
				}
				//printASTNodeInformation("foo", node);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}   
	}
}

/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.Conditional;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.Iteration;
import fr.n7.stl.block.ast.instruction.Return;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;
	
	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}
	
	/**
	 * AST node for the body of the function
	 */
	protected Block body;

	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
	}
	
	/** Retourne tous les types retourné dans le block et dans ses sous blocks */
	static Type getReturnBlockType(Type _type) {
		Type res = _type;
		List<Instruction> instructions = block.getInstructions();
		
		for(Instruction i : instructions) {
			if (i instanceof Return) {
				res.add(((Return) i).getValue().getType());
			} else if (i instanceof Conditional) {
				res.addAll(getReturnBlockType(((Conditional) i).getThenBranch()));
				
				if(((Conditional) i).getElseBranch().isPresent()) {
					res.addAll(getReturnBlockType(((Conditional) i).getElseBranch().get()));
				}
			} else if (i instanceof Iteration) {
				res.addAll(getReturnBlockType(((Iteration) i).getBody()));
			} 
		}
		return res;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean retour;
		boolean args_resolve = true;
		ParameterDeclaration declaration_parametre; 
		// creer une nouvelle table des symboles pour le body 
		HierarchicalScope<Declaration> tableBody= new SymbolTable(_scope);
		Iterator<ParameterDeclaration> it = this.parameters.iterator();
		while(it.hasNext()) {
			declaration_parametre = it.next();
			if (tableBody.accepts(declaration_parametre)) {
				_scope.register(declaration_parametre);
				args_resolve = true;
			}else{
				args_resolve = false;
			}
		}
		if (_scope.accepts(this)) {
			_scope.register(this);
			retour = true & this.type.resolve(_scope) & this.body.resolve(tableBody) & args_resolve;
		}else{
			retour=false;
		}
		return retour;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		throw new SemanticsUndefinedException( "Semantics checkType is undefined in FunctionDeclaration.");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		throw new SemanticsUndefinedException( "Semantics allocateMemory is undefined in FunctionDeclaration.");
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		throw new SemanticsUndefinedException( "Semantics getCode is undefined in FunctionDeclaration.");
	}

}

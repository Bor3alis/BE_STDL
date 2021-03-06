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
import fr.n7.stl.block.ast.type.AtomicType;
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
		int dep = 0;
		while(it.hasNext()) {
				
			declaration_parametre = it.next();
			declaration_parametre.setOffset(dep);
			dep += 1;
			
			if (tableBody.accepts(declaration_parametre)) {
				tableBody.register(declaration_parametre);
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

	public Block getBody() {
		return body;
	}

	/* (non-Javadoc)
         * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
         */
	@Override
	public boolean checkType() {

		return this.type.compatibleWith(this.body.getReturnType()) && this.body.checkType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.body.allocateMemory(Register.LB, _offset);
		for (ParameterDeclaration p : parameters) {
			p.setOffset(_offset+ p.getOffset());
		}
		return taille_parametres(); // déclaration de fonction => ne prend pas de place en mémoire
				}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		String labelFct = this.name;
		String labelFinFct = this.name.concat("_");
		Fragment codeBody = this.body.getCode(_factory);
		Fragment code = _factory.createFragment();

		codeBody.addPrefix(labelFct.concat(":"));
		code.add(_factory.createPush(taille_parametres()));
		code.add(_factory.createJump(labelFinFct));

		// On reserve l'espace pour le retour
		code.add(_factory.createPush(this.getType().length()));

		// On reserve l'espace pour les paramètres
		code.add(_factory.createPush(taille_parametres()));



		code.append(codeBody);

		code.addSuffix(labelFinFct.concat(":"));



		return code;
	}

	@Override
	public Type getReturnType() {
		return AtomicType.VoidType;
	}

	public  int taille_parametres(){
		int res = 0;
		for (ParameterDeclaration p : parameters) {
			res += p.getType().length();
		}
		return res;
	}

	
}

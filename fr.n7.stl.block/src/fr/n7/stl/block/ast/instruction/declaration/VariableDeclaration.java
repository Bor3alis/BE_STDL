/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.TAMInstruction; 
import fr.n7.stl.tam.ast.impl.FragmentImpl;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a variable declaration instruction.
 * @author Marc Pantel
 *
 */
public class VariableDeclaration implements Declaration, Instruction {

	/**
	 * Name of the declared variable.
	 */
	protected String name;
	
	/**
	 * AST node for the type of the declared variable.
	 */
	protected Type type;
	
	/**
	 * AST node for the initial value of the declared variable.
	 */
	protected Expression value;
	
	/**
	 * Address register that contains the base address used to store the declared variable.
	 */
	protected Register register;
	
	/**
	 * Offset from the base address used to store the declared variable
	 * i.e. the size of the memory allocated to the previous declared variables
	 */
	protected int offset;
	
	/**
	 * Creates a variable declaration instruction node for the Abstract Syntax Tree.
	 * @param _name Name of the declared variable.
	 * @param _type AST node for the type of the declared variable.
	 * @param _value AST node for the initial value of the declared variable.
	 */
	public VariableDeclaration(String _name, Type _type, Expression _value) {
		this.name = _name;
		this.type = _type;
		this.value = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.type + " " + this.name + " = " + this.value + ";\n";
	}

	/**
	 * Synthesized semantics attribute for the type of the declared variable.
	 * @return Type of the declared variable.
	 */
	public Type getType() {
		return this.type;
	}

	/* (non-Javadoc)
	 * @see fr.n7.block.ast.VariableDeclaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Synthesized semantics attribute for the register used to compute the address of the variable.
	 * @return Register used to compute the address where the declared variable will be stored.
	 */
	public Register getRegister() {
		return this.register;
	}
	
	/**
	 * Synthesized semantics attribute for the offset used to compute the address of the variable.
	 * @return Offset used to compute the address where the declared variable will be stored.
	 */
	public int getOffset() {
		return this.offset;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		if(_scope.contains(this.name)){
			Logger.error("The identifier " + this.name + " has already been declared.");
		} else
		{
			if(_scope.accepts(this)) {
				_scope.register(this);
			}
		//	Declaration info = _scope.get(name);
			
		}
		
		return this.value.resolve(_scope) && this.type.resolve(_scope);

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		
		Type treel = this.value.getType();
		
		if(!treel.compatibleWith(this.type)) {
			System.out.println(treel);
			System.out.println(type);
			Logger.error("the variable " + this.name + " has the type " + type + " but should be type of " + treel);
			return false;
		} else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.register = _register;
		this.offset = _offset;
		return this.getType().length();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
	
		Fragment code = _factory.createFragment();
		code.add(_factory.createPush(this.getType().length()));	
		code.append(this.value.getCode(_factory));
		code.add(_factory.createStore(Register.LB, this.offset, this.getType().length()));
		code.addComment("VariableDeclaration Fin");
		return code;
	}

	@Override
	public Type getReturnType() {
		return AtomicType.VoidType;
	}

}

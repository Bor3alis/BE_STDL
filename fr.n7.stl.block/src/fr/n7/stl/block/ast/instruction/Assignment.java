/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.block.ast.expression.accessible.AddressAccess;
import fr.n7.stl.block.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.block.ast.instruction.declaration.ConstantDeclaration;
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
 * Implementation of the Abstract Syntax Tree node for an array type.
 * @author Marc Pantel
 *
 */
public class Assignment implements Instruction, Expression {

	protected Expression value;
	protected AssignableExpression assignable;

	/**
	 * Create an assignment instruction implementation from the assignable expression
	 * and the assigned value.
	 * @param _assignable Expression that can be assigned a value.
	 * @param _value Value assigned to the expression.
	 */
	public Assignment(AssignableExpression _assignable, Expression _value) {
		this.assignable = _assignable;
		this.value = _value;
		/* This attribute will be assigned to the appropriate value by the resolve action */
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.assignable + " = " + this.value.toString() + ";\n";
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		/*if(!_scope.knows(this.value.toString())) {
			Logger.error("Erreur Assignment");
		} else {
			Declaration info = _scope.get(this.value.toString());
			if(info instanceof ConstantDeclaration){
				Logger.error("Erreur Assignment azeezft");
			}
		} */
		
		 return this.assignable.resolve(_scope) && this.value.resolve(_scope);
	

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#getType()
	 */
	@Override
	public Type getType() {
		return this.assignable.getType();
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		return this.assignable.getType().compatibleWith(this.value.getType());
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		//return value.getType().length();
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		Fragment code_suite = assignable.getCode(_factory);
		
		
		frag.append(value.getCode(_factory));
		
		// on récupère la valeur de v à partir de son adresse
		if (this.value instanceof AccessibleExpression && ! (this.value instanceof AddressAccess))
			frag.add(_factory.createLoadI(this.value.getType().length()));
		
		
		TAMInstruction instr = _factory.createStoreI(this.value.getType().length());
		frag.append(code_suite);
		

		
		frag.add(instr);
		
		return frag;
	}

	@Override
	public Type getReturnType() {
		return AtomicType.VoidType;
	}

}

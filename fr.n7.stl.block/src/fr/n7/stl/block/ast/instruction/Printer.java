/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.expression.accessible.AccessibleExpression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.CoupleType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Implementation of the Abstract Syntax Tree node for a printer instruction.
 * @author Marc Pantel
 *
 */
public class Printer implements Instruction {

	protected Expression parameter;

	public Printer(Expression _value) {
		this.parameter = _value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "print " + this.parameter + ";\n";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.nnope7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.parameter.resolve(_scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		Type te = this.parameter.getType();
		return (! te.compatibleWith(AtomicType.ErrorType));
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	// ATTENTION PRINT INT / FLOAT
	@Override
	public Fragment getCode(TAMFactory _factory) {
		
		Fragment code = _factory.createFragment();
		code.append(this.parameter.getCode(_factory));
		
		if (this.parameter instanceof AccessibleExpression)
			code.add(_factory.createLoadI(this.parameter.getType().length()));
		
		Type type = this.parameter.getType();
		
		if(type.equals(AtomicType.BooleanType)) {
			code.add(Library.BOut);
		} else if(type.equals(AtomicType.CharacterType)) {
			code.add(Library.COut);
		} else if (type.equals(AtomicType.IntegerType)) {
			code.add(Library.IOut);
		} else if(type.equals(AtomicType.StringType)) {
			code.add(Library.SOut);
		} 
		return code;
		
	}

	@Override
	public Type getReturnType() {
		return AtomicType.VoidType;
	}

}

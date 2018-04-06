/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Optional<Block> elseBranch;
	
	

	public Block getThenBranch() {
		return thenBranch;
	}

	public Optional<Block> getElseBranch() {
		return elseBranch;
	}

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = Optional.of(_else);
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = Optional.empty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch.isPresent())?(" else " + this.elseBranch.get()):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean res_condition = this.condition.resolve(_scope);
		boolean res_then = this.thenBranch.resolve(_scope);
		boolean res_else = true;
		if (this.elseBranch.isPresent()) {
			res_else = this.elseBranch.get().resolve(_scope);
		}
		return res_then & res_condition & res_else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		boolean _result = true;
		
		_result &= this.condition.getType().compatibleWith(AtomicType.BooleanType);
		_result &= this.thenBranch.checkType();
		
		if(this.elseBranch.isPresent()) {
			_result &= this.elseBranch.get().checkType();
		}
		return _result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register _register, int _offset) {
		this.thenBranch.allocateMemory(_register, _offset);
		if(this.elseBranch.isPresent()) {
			this.elseBranch.get().allocateMemory(_register, _offset);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment code = condition.getCode(_factory);
		String labelElse;
		String labelFin = "finIf".concat(String.valueOf(_factory.createLabelNumber()));
		if (this.elseBranch.isPresent()) {
			labelElse = "else".concat(String.valueOf(_factory.createLabelNumber()));
		} else {
			labelElse = labelFin;
		}
		
		code.add(_factory.createJumpIf(labelElse, 0));
		code.append(thenBranch.getCode(_factory));
		if (this.elseBranch.isPresent()) {
			code.add(_factory.createJump(labelFin));
			code.addSuffix(labelElse.concat(":"));
			code.append(this.elseBranch.get().getCode(_factory));
			
		}
		code.addSuffix(labelFin.concat(":"));
		return code;
		
	}

	@Override
	public Type getReturnType() {
		Type res = AtomicType.VoidType;
		res = res.merge(this.thenBranch.getReturnType());
		if(this.elseBranch.isPresent()) {
			res = res.merge(this.elseBranch.get().getReturnType());
		}
		return res;

	}

}

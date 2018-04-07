/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractUse;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Implementation of the Abstract Syntax Tree node for a variable use expression.
 * @author Marc Pantel
 * TODO : Should also hold a function and not only a variable.
 */
public class VariableUse extends AbstractUse {
	
	protected VariableDeclaration declaration;
	
	/**
	 * Creates a variable use expression Abstract Syntax Tree node.
	 * @param _name Name of the used variable.
	 */
	public VariableUse(VariableDeclaration _declaration) {
		this.declaration = _declaration;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getDeclaration()
	 */
	public Declaration getDeclaration() {
		return this.declaration;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.AbstractUse#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		/*Fragment _result = _factory.createFragment();
		_result.add(_factory.createLoad(
				Register.LB,
				this.declaration.getOffset(),
				this.declaration.getType().length()));
		_result.addComment(this.toString());
		return _result;*/

		Fragment _result = _factory.createFragment();
		_result.add(_factory.createLoadL(this.declaration.getOffset()));
		return _result;
	}
	
	/* 		Fragment code = new FragmentImpl();
		code.add(_factory.createPush(this.getType().length()));	
		code.append(this.value.getCode(_factory));
		code.addComment("VariableDeclaration Fin");
		return code; */

}

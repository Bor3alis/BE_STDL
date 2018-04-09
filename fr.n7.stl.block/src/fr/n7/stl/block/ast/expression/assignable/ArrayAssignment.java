/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a cell in an array.
 * @author Marc Pantel
 */
public class ArrayAssignment extends AbstractArray implements AssignableExpression {

	/**
	 * Construction for the implementation of an array element assignment expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element assignment expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element assignment expression.
	 */
	public ArrayAssignment(AssignableExpression _array, Expression _index) {
		super(_array, _index);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.ArrayAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		//throw new SemanticsUndefinedException( "Semantics getCode is undefined in ArrayAllocation.");
		Fragment code = _factory.createFragment();
		
		code.append(this.array.getCode(_factory));
		code.add(_factory.createLoadI(1));
		code.append(this.index.getCode(_factory));
		code.add(_factory.createLoadL(this.array.getType().length()));

		code.add(Library.IMul);
		code.add(Library.IAdd);


		
		//code.add(_factory.createLoadI(this.array.getType().length()));
		
		
		/*code.append(this.index.getCode(_factory));
		code.add(_factory.createLoadL(this.array.getType().length())); // A VERIFIER
		code.add(Library.IMul);
		code.add(Library.IAdd);*/
		
		return code;
		
	}

	
}

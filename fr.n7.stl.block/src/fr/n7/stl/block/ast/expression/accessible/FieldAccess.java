/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAccess extends AbstractField implements Expression {

	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public FieldAccess(Expression _record, String _name) {
		super(_record, _name);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	
	@Override
	public Fragment getCode(TAMFactory _factory) {
		
		FieldDeclaration previous_field = ((RecordType)this.record.getType()).get_previousField(this.field);
		FieldDeclaration next_field = ((RecordType)this.record.getType()).get_nextField(this.field);
		
		Fragment code = _factory.createFragment();
		code.append(this.record.getCode(_factory));

		code.add(_factory.createLoadI(this.record.getType().length()));

		if(! (previous_field == null)) {
			System.out.println("AAAAAA");
			code.add(_factory.createPop(this.field.getType().length(), previous_field.getType().length()));
		}
		
		if(! (next_field == null)) {
			System.out.println("BBBBBB");
			code.add(_factory.createPop(0,next_field.getType().length()));
		}

		
		
		return code;
		

}
}

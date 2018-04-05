/**
 * 
 */
package fr.n7.stl.block.ast;

import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.instruction.Conditional;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.Iteration;
import fr.n7.stl.block.ast.instruction.Return;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Represents a Block node in the Abstract Syntax Tree node for the Bloc language.
 * Declares the various semantics attributes for the node.
 * 
 * A block contains declarations. It is thus a Scope even if a separate SymbolTable is used in
 * the attributed semantics in order to manage declarations.
 * 
 * @author Marc Pantel
 *
 */
public class Block {

	/**
	 * Sequence of instructions contained in a block.
	 */
	protected List<Instruction> instructions;

	
	public List<Instruction> getInstructions() {
		return instructions;
	}

	/**
	 * Constructor for a block.
	 */
	public Block(List<Instruction> _instructions) {
		this.instructions = _instructions;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _local = "";
		for (Instruction _instruction : this.instructions) {
			_local += _instruction;
		}
		return "{\n" + _local + "}\n" ;
	}
	
	
	
	/**
	 * Inherited Semantics attribute to check that all identifiers have been defined and
	 * associate all identifiers uses with their definitions.
	 * @param _scope Inherited Scope attribute that contains the defined identifiers.
	 * @return Synthesized Semantics attribute that indicates if the identifier used in the
	 * block have been previously defined.
	 */
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
	boolean _result = true;
		
		SymbolTable t = new SymbolTable(_scope);
		
		for(Instruction i : instructions) {
			_result &= i.resolve(t);
		}
		
		return _result;

	}
	public Type getReturnType() {
		Type res = AtomicType.VoidType;
		for (Instruction i :  instructions) {
			res =res.merge(i.getReturnType());	
			}
		return res;
	}

	/**
	 * Synthesized Semantics attribute to check that an instruction if well typed.
	 * @return Synthesized True if the instruction is well typed, False if not.
	 */	
	public boolean checkType() {
		boolean _result = true;
		for(Instruction i : instructions) {
			_result &= i.checkType();
		}
		return _result;
	}

	/**
	 * Inherited Semantics attribute to allocate memory for the variables declared in the instruction.
	 * Synthesized Semantics attribute that compute the size of the allocated memory. 
	 * @param _register Inherited Register associated to the address of the variables.
	 * @param _offset Inherited Current offset for the address of the variables.
	 */	
	public void allocateMemory(Register _register, int _offset) {
		int d = _offset;
		
		for(Instruction i : this.instructions) {
			d += i.allocateMemory(_register, d);
		}
		
	}

	/**
	 * Inherited Semantics attribute to build the nodes of the abstract syntax tree for the generated TAM code.
	 * Synthesized Semantics attribute that provide the generated TAM code.
	 * @param _factory Inherited Factory to build AST nodes for TAM code.
	 * @return Synthesized AST for the generated TAM code.
	 */
	public Fragment getCode(TAMFactory _factory) {
		int taille_bloc = 0;
		Fragment code = new FragmentImpl();
		
		for(Instruction i : this.instructions) {
			code.append(i.getCode(_factory));
			
			if(i instanceof VariableDeclaration) {
				taille_bloc += 1;
			}
			
		}
		
		code.add(_factory.createPop(0, taille_bloc));
		code.add(_factory.createHalt());
		return code;
	}
	
	
	

}

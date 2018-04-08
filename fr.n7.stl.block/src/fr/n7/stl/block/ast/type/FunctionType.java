/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;

/**
 * Implementation of the Abstract Syntax Tree node for a function type.
 * @author Marc Pantel
 *
 */
public class FunctionType implements Type {

	private Type result;
	private List<Type> parameters;

	public FunctionType(Type _result, Iterable<Type> _parameters) {
		this.result = _result;
		this.parameters = new LinkedList<Type>();
		for (Type _type : _parameters) {
			this.parameters.add(_type);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if(_other instanceof FunctionType) {
			FunctionType _local = (FunctionType) _other;
			if(this.parameters.size() == _local.parameters.size()) {
				Iterator<Type> i1 = this.parameters.iterator();
				Iterator<Type> i2 = _local.parameters.iterator();
				boolean _result = true;
				while (i1.hasNext() && i2.hasNext() && _result) {
					_result = _result && (i1.next().equalsTo(i2.next()));
				}
				return _result;
			}  else {
			return false;
			}
		} else {
			return false;
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if(_other instanceof FunctionType){
			FunctionType _local = (FunctionType) _other;
			if(this.parameters.size() == _local.parameters.size()) {
				Iterator<Type> i1 = this.parameters.iterator();
				Iterator<Type> i2 = _local.parameters.iterator();
				boolean _result = true;
				while(i1.hasNext() && i2.hasNext() && _result) {
					_result = _result && (i1.next().compatibleWith(i2.next()));
				}
				return _result &&  this.result.compatibleWith(_local.result);
			} else {
				return false;
			} 
		} else {
			return false;
		}

	} 
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if(_other instanceof FunctionType) {
			FunctionType _local = (FunctionType)_other;

			Type _result = this.result.merge(_local.result);
			List<Type> _parameters = new LinkedList<Type>();

			if(this.parameters.size() == _local.parameters.size()) {
				Iterator<Type> i1 = this.parameters.iterator();
				Iterator<Type> i2 = _local.parameters.iterator();
				while (i1.hasNext() && i2.hasNext()) {
					_parameters.add(i1.next().merge(i2.next()));
				}
				FunctionType _function = new FunctionType(_result, _parameters);
				return _function;
			} else {
				return AtomicType.ErrorType;
			}

		} else {
			return AtomicType.ErrorType;
		}

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		int _length = 0;
		for(Type p : this.parameters) {
			_length += p.length();
		}
		return _length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "(";
		Iterator<Type> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + ") -> " + this.result;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _result = true;
		int dep = 0;
		for(Type p : parameters) {
			_result &= p.resolve(_scope);
		}
		_result &= result.resolve(_scope);
		return _result;
		
	
	}

}

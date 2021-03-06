/**
 * 
 */
package fr.n7.stl.block.ast.type;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.Scope;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a record type.
 * This one is a scope to allow an easy access to the fields.
 * @author Marc Pantel
 *
 */
public class RecordType implements Type, Declaration, Scope<FieldDeclaration> {

	private List<FieldDeclaration> fields;
	private String name;

	/**
	 * Constructor for a record type including fields.
	 * @param _name Name of the record type.
	 * @param _fields Sequence of fields to initialize the content of the record type.
	 */
	public RecordType(String _name, Iterable<FieldDeclaration> _fields) {
		this.name = _name;
		this.fields = new LinkedList<FieldDeclaration>();
		for (FieldDeclaration _field : _fields) {
			this.fields.add(_field);
		}
	}

	/**
	 * Constructor for an empty record type (i.e. without fields).
	 * @param _name Name of the record type.
	 */
	public RecordType(String _name) {
		this.name = _name;
		this.fields = new LinkedList<FieldDeclaration>();
	}

	/**
	 * Add a field to a record type.
	 * @param _field The added field.
	 */
	public void add(FieldDeclaration _field) {
		this.fields.add(_field);
	}

	/**
	 * Add a sequence of fields to a record type.
	 * @param _fields : Sequence of fields to be added.
	 */
	public void addAll(Iterable<FieldDeclaration> _fields) {
		for (FieldDeclaration _field : _fields) {
			this.fields.add(_field);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if(_other instanceof RecordType) {
			return this.fields.equals(((RecordType)_other).fields) && this.name.equals(((RecordType)_other).name);
		} else {
			return false;
		}
	}

	@Override
	public boolean compatibleWith(Type _other) {

		if(_other instanceof RecordType) {
			RecordType _local = (RecordType) _other;
			if(this.fields.size() == _local.fields.size()) {
				Iterator<FieldDeclaration> i1 = this.fields.iterator();
				Iterator<FieldDeclaration> i2 = _local.fields.iterator();
				boolean _result = true;
				while (i1.hasNext() && i2.hasNext() && _result) {
					_result = _result && (i1.next().getType().compatibleWith(i2.next().getType()));
				}
				return _result;
			} else {
				return false;
			}
		}else if ( _other instanceof NamedType ){
			return _other.compatibleWith(this);
		} else {
			return false;
		}

	}

	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if(_other instanceof RecordType) {
			RecordType _local = (RecordType)_other;
			
			List<FieldDeclaration> _fields =  new LinkedList<FieldDeclaration>();
			
			if(this.fields.size() == _local.fields.size()) {
				Iterator<FieldDeclaration> i1 = this.fields.iterator();
				Iterator<FieldDeclaration> i2 = _local.fields.iterator();
				while(i1.hasNext() && i2.hasNext()) {
					_fields.add(i1.next().merge(i2.next()));
				} 
			} else {
				return AtomicType.ErrorType;
			}
		} else {
			return AtomicType.ErrorType;
		} 
		
		return AtomicType.ErrorType;

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#get(java.lang.String)
	 */
	@Override
	public FieldDeclaration get(String _name) {
		boolean _found = false;
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		FieldDeclaration _current = null;
		while (_iter.hasNext() && (! _found)) {
			_current = _iter.next();
			_found = _found || _current.getName().contentEquals(_name);
		}
		if (_found) {
			return _current;
		} else {
			return null;
		}
	}
	
	public FieldDeclaration get_previousField(FieldDeclaration _field){
		int idx = this.fields.indexOf(_field);
		if(idx <= 0) {
			return null;
		} else {
			return this.fields.get(idx -1);
		}
	}
	
	public FieldDeclaration get_nextField(FieldDeclaration _field) {
		int idx = this.fields.indexOf(_field);
		if(idx < 0 || idx+1 == this.fields.size()){
			return null;
		} else {
			return this.fields.get(idx + 1);
		}
	}
	
	

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String _name) {
		boolean _result = false;
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		
		while (_iter.hasNext() && (! _result)) {
			if(_iter.hasNext()) 
			
			
			_result = _result || _iter.next().getName().contentEquals(_name);
		}
		return _result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#accepts(fr.n7.stl.block.ast.Declaration)
	 */
	@Override
	public boolean accepts(FieldDeclaration _declaration) {
		return ! this.contains(_declaration.getName());
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Scope#register(fr.n7.stl.block.ast.Declaration)
	 */
	@Override
	public void register(FieldDeclaration _declaration) {
		if (this.accepts(_declaration)) {
			this.fields.add(_declaration);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Build a sequence type by erasing the names of the fields.
	 * @return Sequence type extracted from record fields.
	 */
	public SequenceType erase() {
		SequenceType _local = new SequenceType();
		for (FieldDeclaration _field : this.fields) {
			_local.add(_field.getType());
		}
		return _local;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length()
	 */
	@Override
	public int length() {
		int _length = 0;
		for (FieldDeclaration f : this.fields) {
			_length += f.getType().length();
		}
		return _length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = "struct " + this.name + " { ";
		Iterator<FieldDeclaration> _iter = this.fields.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " " + _iter.next();
			}
		}
		return _result + "}";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		boolean _result = true;
		int dep = 0;
		for (FieldDeclaration f : this.fields) {
			f.setOffset(dep);
			_result = _result && f.getType().resolve(_scope);
			dep += 1;
		}
		return _result;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.scope.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this;
	}
}

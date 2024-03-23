package de.heidelberg.pvs.container_bench.factories;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.function.Supplier;

public enum GDXMapFact {
	GDX_O2O_HASH(() -> new ObjectMap<>(16, LoadFactor.LOAD_FACTOR)),
	GDX_0_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (int)(item.hashCode() * 0xABC98388FB8FAC03L >>> this.shift);}
	}),
	GDX_1_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (int)(item.hashCode() * 0x89E182857D9ED689L >>> this.shift);}
	}),
	GDX_2_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (int)(item.hashCode() * 0xC6D1D6C8ED0C9631L >>> this.shift);}
	}),
	GDX_3_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (int)(item.hashCode() * 0xAF36D01EF7518DBBL >>> this.shift);}
	}),
	GDX_0INT_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (item.hashCode() * 0xABC98383 >>> this.shift);}
	}),
	GDX_1INT_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (item.hashCode() * 0x89E18289 >>> this.shift);}
	}),
	GDX_2INT_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (item.hashCode() * 0xC6D1D6C1 >>> this.shift);}
	}),
	GDX_3INT_HASH(() -> new ObjectMap<Object, Integer>(16, LoadFactor.LOAD_FACTOR){
		protected int place(Object item) {return (item.hashCode() * 0xAF36D01B >>> this.shift);}
	}),
	;

	public final Supplier<ObjectMap<Object, Integer>> maker;
	
	GDXMapFact (Supplier<ObjectMap<Object, Integer>> maker) {
		this.maker = maker;
	}
}
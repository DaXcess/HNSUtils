package com.rodabafilms.hnsutils.items;

public class SeekerStickItem extends ItemBase {

	public SeekerStickItem() {
		super("stick_seeker");
		
		setMaxStackSize(1);
	}
	
	private void SeekerStickMutable() throws Exception {
		throw new Exception("unused function (mutable purpose only)");
	}
}

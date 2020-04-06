package com.rodabafilms.hnsutils.items;

public class HiderStickItem extends ItemBase {

	public HiderStickItem() {
		super("stick_hider");
		
		setMaxStackSize(1);
	}
	
	private void HiderStickMutable() throws Exception {
		throw new Exception("unused function (mutable purpose only)");
	}
}

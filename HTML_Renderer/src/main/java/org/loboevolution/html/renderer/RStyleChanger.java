/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */
/*
 * Created on Apr 17, 2005
 */
package org.loboevolution.html.renderer;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.loboevolution.html.dombl.ModelNode;
import org.loboevolution.html.renderstate.RenderState;

/**
 * The Class RStyleChanger.
 *
 * @author J. H. S.
 */
public final class RStyleChanger extends BaseRenderable implements Renderable {

	/** The model node. */
	private final ModelNode modelNode;

	/**
	 * Instantiates a new r style changer.
	 *
	 * @param modelNode
	 *            the model node
	 */
	public RStyleChanger(ModelNode modelNode) {
		this.modelNode = modelNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.renderer.Renderable#getModelNode()
	 */
	@Override
	public ModelNode getModelNode() {
		return this.modelNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.xamj.domimpl.markup.Renderable#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		RenderState rs = this.modelNode.getRenderState();
		g.setColor(rs.getColor());
		g.setFont(rs.getFont());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.render.Renderable#invalidate()
	 */
	/**
	 * Invalidate layout up tree.
	 */
	public void invalidateLayoutUpTree() {
		// Method not implemented
	}

	/**
	 * On mouse click.
	 *
	 * @param event
	 *            the event
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void onMouseClick(MouseEvent event, int x, int y) {
		throw new UnsupportedOperationException("unexpected");
	}

	/**
	 * On mouse pressed.
	 *
	 * @param event
	 *            the event
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void onMousePressed(MouseEvent event, int x, int y) {
		throw new UnsupportedOperationException("unexpected");
	}

	/**
	 * On mouse released.
	 *
	 * @param event
	 *            the event
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void onMouseReleased(MouseEvent event, int x, int y) {
		throw new UnsupportedOperationException("unexpected");
	}
}

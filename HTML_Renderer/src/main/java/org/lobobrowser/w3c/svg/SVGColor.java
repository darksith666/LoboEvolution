/*
    GNU GENERAL LICENSE
    Copyright (C) 2006 The Lobo Project. Copyright (C) 2014 - 2017 Lobo Evolution

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
    

    Contact info: lobochief@users.sourceforge.net; ivan.difrancesco@yahoo.it
 */
package org.lobobrowser.w3c.svg;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

public interface SVGColor extends CSSValue {
	// Color Types
	public static final short SVG_COLORTYPE_UNKNOWN = 0;
	public static final short SVG_COLORTYPE_RGBCOLOR = 1;
	public static final short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
	public static final short SVG_COLORTYPE_CURRENTCOLOR = 3;

	public short getColorType();

	public RGBColor getRGBColor();

	public SVGICCColor getICCColor();

	public void setRGBColor(String rgbColor) throws SVGException;

	public void setRGBColorICCColor(String rgbColor, String iccColor) throws SVGException;

	public void setColor(short colorType, String rgbColor, String iccColor) throws SVGException;
}

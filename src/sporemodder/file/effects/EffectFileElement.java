/****************************************************************************
* Copyright (C) 2019 Eric Mor
*
* This file is part of SporeModder FX.
*
* SporeModder FX is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
****************************************************************************/
package sporemodder.file.effects;

import java.io.IOException;
import java.util.List;

import sporemodder.file.filestructures.StreamReader;
import sporemodder.file.filestructures.StreamWriter;
import sporemodder.file.argscript.ArgScriptWriter;

public interface EffectFileElement {
	public String getName();
	
	public void read(StreamReader stream) throws IOException;
	public void write(StreamWriter stream) throws IOException;
	
	public void toArgScript(ArgScriptWriter writer);
	
	public boolean isEffectComponent();
	
	public ElementFactory getFactory();
	
	default public List<EffectFileElement> getUsedElements() {
		return null;
	}
	
	public interface ElementFactory {
		public int getTypeCode();
	}
}

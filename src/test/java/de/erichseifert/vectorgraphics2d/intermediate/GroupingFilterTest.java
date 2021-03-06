/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2015 Erich Seifert <dev[at]erichseifert.de>
 *
 * This file is part of VectorGraphics2D.
 *
 * VectorGraphics2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VectorGraphics2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with VectorGraphics2D.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.erichseifert.vectorgraphics2d.intermediate;

import static org.junit.Assert.assertEquals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Iterator;

import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.CommandStream;
import de.erichseifert.vectorgraphics2d.intermediate.Filter;
import de.erichseifert.vectorgraphics2d.intermediate.Group;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetStrokeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.StateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.filters.GroupingFilter;

public class GroupingFilterTest {
	@Test public void filtered() {
		CommandStream resultStream = new CommandStream();
		resultStream.add(null, new SetColorCommand(Color.BLACK));
		resultStream.add(null, new SetStrokeCommand(new BasicStroke(1f)));
		resultStream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
		resultStream.add(null, new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
		resultStream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

		CommandStream expectedStream = new CommandStream();
		Iterator<Command<?>> resultCloneIterator = resultStream.iterator();
		Group group1 = new Group();
		group1.add(resultCloneIterator.next());
		group1.add(resultCloneIterator.next());
		expectedStream.add(null, group1);
		expectedStream.add(null, resultCloneIterator.next());
		Group group2 = new Group();
		group2.add(resultCloneIterator.next());
		expectedStream.add(null, group2);
		expectedStream.add(null, resultCloneIterator.next());
		Iterator<Command<?>> expectedIterator = expectedStream.iterator();

		Filter resultIterator = new GroupingFilter(resultStream) {
			@Override
			protected boolean isGrouped(Command<?> command) {
				return command instanceof StateCommand;
			}
		};

		for (; resultIterator.hasNext() || expectedIterator.hasNext();) {
			Command<?> result = resultIterator.next();
			Command<?> expected = expectedIterator.next();
			assertEquals(expected, result);
		}
	}
}


/*
 * Copyright (c) 2005-2010 Flamingo / Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.substance.flamingo.ribbon.ui;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.plaf.*;

import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.internal.ui.ribbon.*;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.substance.api.*;
import org.pushingpixels.substance.api.painter.border.SubstanceBorderPainter;
import org.pushingpixels.substance.internal.painter.*;
import org.pushingpixels.substance.internal.utils.*;

/**
 * UI for ribbon in <b>Substance</b> look and feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceRibbonUI extends BasicRibbonUI {
	/**
	 * Panel for hosting task toggle buttons.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class SubstanceTaskToggleButtonsHostPanel extends
			TaskToggleButtonsHostPanel {
		@Override
		protected void paintContextualTaskGroupOutlines(Graphics g,
				RibbonContextualTaskGroup group, Rectangle groupBounds) {
			Graphics2D g2d = (Graphics2D) g.create();

			// SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
			// .getBorderColorScheme(ribbon, ComponentState.DEFAULT);

			g2d.translate(groupBounds.x, 0);
			SeparatorPainterUtils.paintSeparator(ribbon, g2d, 2,
					groupBounds.height * 3 / 4, SwingConstants.VERTICAL, false,
					0, groupBounds.height / 3, true);

			g2d.translate(groupBounds.width - 1, 0);
			SeparatorPainterUtils.paintSeparator(ribbon, g2d, 2,
					groupBounds.height * 3 / 4, SwingConstants.VERTICAL, false,
					0, groupBounds.height / 3, true);

			g2d.dispose();
		}

		@Override
		protected void paintTaskOutlines(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			SubstanceColorScheme scheme = SubstanceColorSchemeUtilities
					.getColorScheme(ribbon,
							ColorSchemeAssociationKind.SEPARATOR,
							ComponentState.ENABLED);

			Set<RibbonTask> tasksWithTrailingSeparators = new HashSet<RibbonTask>();
			// add all regular tasks except the last
			for (int i = 0; i < ribbon.getTaskCount() - 1; i++) {
				RibbonTask task = ribbon.getTask(i);
				tasksWithTrailingSeparators.add(task);
				// System.out.println("Added " + task.getTitle());
			}
			// add all tasks of visible contextual groups except last task in
			// each group
			for (int i = 0; i < ribbon.getContextualTaskGroupCount(); i++) {
				RibbonContextualTaskGroup group = ribbon
						.getContextualTaskGroup(i);
				if (ribbon.isVisible(group)) {
					for (int j = 0; j < group.getTaskCount() - 1; j++) {
						RibbonTask task = group.getTask(j);
						tasksWithTrailingSeparators.add(task);
					}
				}
			}

			for (RibbonTask taskWithTrailingSeparator : tasksWithTrailingSeparators) {
				JRibbonTaskToggleButton taskToggleButton = taskToggleButtons
						.get(taskWithTrailingSeparator);
				Rectangle bounds = taskToggleButton.getBounds();
				int x = bounds.x + bounds.width + getTabButtonGap() / 2 - 1;
				g2d.translate(x, 0);
				SeparatorPainterUtils.paintSeparator(ribbon, g2d, scheme, 2,
						getHeight(), SwingConstants.VERTICAL, false,
						getHeight() / 3, 0, true);
				g2d.translate(-x, 0);
			}

			g2d.dispose();
		}
	}

	/**
	 * Panel for hosting ribbon bands.
	 * 
	 * @author Kirill Grouchnikov
	 */
	protected class SubstanceBandHostPanel extends BandHostPanel {
		@Override
		protected void paintComponent(Graphics g) {
			int dy = 0;
			for (int i = 0; i < getComponentCount(); i++) {
				Component child = getComponent(i);
				if (child instanceof AbstractRibbonBand) {
					dy = child.getBounds().y;
					break;
				}
			}
			SubstanceRibbonBandUI.paintRibbonBandBackground(g, this, 0.0f, dy);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#createUI(javax.swing.JComponent)
	 */
	public static ComponentUI createUI(JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SubstanceRibbonUI();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#installDefaults()
	 */
	@Override
	protected void installDefaults() {
		super.installDefaults();
		SubstanceLookAndFeel.setDecorationType(this.ribbon,
				DecorationAreaType.HEADER);
		Color backgr = this.ribbon.getBackground();
		if (backgr == null || backgr instanceof UIResource) {
			Color toSet = SubstanceColorSchemeUtilities.getColorScheme(
					this.ribbon, ComponentState.ENABLED)
					.getBackgroundFillColor();
			this.ribbon.setBackground(new ColorUIResource(toSet));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#uninstallDefaults()
	 */
	@Override
	protected void uninstallDefaults() {
		DecorationPainterUtils.clearDecorationType(this.ribbon);
		super.uninstallDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#installComponents()
	 */
	@Override
	protected void installComponents() {
		super.installComponents();
		SubstanceLookAndFeel.setDecorationType(this.taskBarPanel,
				DecorationAreaType.PRIMARY_TITLE_PANE);
		SubstanceLookAndFeel.setDecorationType(this.ribbon,
				DecorationAreaType.HEADER);
		SubstanceLookAndFeel.setDecorationType(this.bandScrollablePanel,
				DecorationAreaType.GENERAL);
	}

	@Override
	protected void uninstallComponents() {
		DecorationPainterUtils.clearDecorationType(this.taskBarPanel);
		super.uninstallComponents();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#createTaskToggleButtonsHostPanel
	 * ()
	 */
	@Override
	protected TaskToggleButtonsHostPanel createTaskToggleButtonsHostPanel() {
		return new SubstanceTaskToggleButtonsHostPanel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#createBandHostPanel()
	 */
	@Override
	protected BandHostPanel createBandHostPanel() {
		return new SubstanceBandHostPanel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#paintBackground(java.awt.Graphics
	 * )
	 */
	@Override
	protected void paintBackground(Graphics g) {
		BackgroundPaintingUtils.update(g, this.ribbon, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#paintTaskArea(java.awt.Graphics
	 * , int, int, int, int)
	 */
	@Override
	protected void paintTaskArea(Graphics g, int x, int y, int width, int height) {
		if (this.ribbon.getTaskCount() == 0)
			return;

		Graphics2D g2d = (Graphics2D) g.create();

		RibbonTask selectedTask = this.ribbon.getSelectedTask();
		JRibbonTaskToggleButton selectedTaskButton = this.taskToggleButtons
				.get(selectedTask);
		Rectangle selectedTaskButtonBounds = selectedTaskButton.getBounds();
		Point converted = SwingUtilities.convertPoint(selectedTaskButton
				.getParent(), selectedTaskButtonBounds.getLocation(),
				this.ribbon);
		float radius = SubstanceSizeUtils
				.getClassicButtonCornerRadius(SubstanceSizeUtils
						.getComponentFontSize(this.ribbon));

		int borderDelta = (int) Math.floor(SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(this.ribbon)) / 2.0);

		SubstanceBorderPainter borderPainter = SubstanceCoreUtilities
				.getBorderPainter(this.ribbon);
		int borderThickness = (int) SubstanceSizeUtils
				.getBorderStrokeWidth(SubstanceSizeUtils
						.getComponentFontSize(this.ribbon));

		AbstractRibbonBand band = (selectedTask.getBandCount() == 0) ? null
				: selectedTask.getBand(0);
		SubstanceColorScheme borderScheme = SubstanceColorSchemeUtilities
				.getColorScheme(band, ColorSchemeAssociationKind.BORDER,
						ComponentState.ENABLED);

		Rectangle taskToggleButtonsViewportBounds = taskToggleButtonsScrollablePanel
				.getView().getParent().getBounds();
		taskToggleButtonsViewportBounds.setLocation(SwingUtilities
				.convertPoint(taskToggleButtonsScrollablePanel,
						taskToggleButtonsViewportBounds.getLocation(),
						this.ribbon));
		int startSelectedX = Math.max(converted.x + 1,
				(int) taskToggleButtonsViewportBounds.getMinX());
		startSelectedX = Math.min(startSelectedX,
				(int) taskToggleButtonsViewportBounds.getMaxX());
		int endSelectedX = Math.min(converted.x
				+ selectedTaskButtonBounds.width - 1,
				(int) taskToggleButtonsViewportBounds.getMaxX());
		endSelectedX = Math.max(endSelectedX,
				(int) taskToggleButtonsViewportBounds.getMinX());

		Shape outerContour = RibbonBorderShaper.getRibbonBorderOutline(
				this.ribbon, x + borderDelta, x + width - 2 * borderDelta - 1,
				startSelectedX + borderDelta, endSelectedX - 2 * borderDelta,
				converted.y + borderDelta, y + borderDelta, y + height - 2
						* borderDelta, radius);

		Shape innerContour = RibbonBorderShaper.getRibbonBorderOutline(
				this.ribbon, x + borderDelta + borderThickness, x + width - 2
						* (borderDelta + borderThickness) - 1, startSelectedX
						+ borderDelta + borderThickness, endSelectedX - 2
						* (borderDelta + borderThickness), converted.y
						+ borderDelta + borderThickness, y + borderDelta
						+ borderThickness, y + height - 2
						* (borderDelta + borderThickness) + 1, radius);

		g2d.setColor(SubstanceColorSchemeUtilities.getColorScheme(band,
				ComponentState.ENABLED).getBackgroundFillColor());
		g2d.clipRect(x, y, width, height + 2);
		g2d.fill(outerContour);

		borderPainter.paintBorder(g2d, this.ribbon, width, height
				+ selectedTaskButtonBounds.height + 1, outerContour,
				innerContour, borderScheme);

		// check whether the currently selected task is a contextual task
		RibbonTask selected = selectedTask;
		RibbonContextualTaskGroup contextualGroup = selected
				.getContextualGroup();
		if (contextualGroup != null) {
			// paint a small gradient directly below the task area
			Insets ins = this.ribbon.getInsets();
			int topY = ins.top + getTaskbarHeight();
			int bottomY = topY + 5;
			Color hueColor = contextualGroup.getHueColor();
			Paint paint = new GradientPaint(0, topY, FlamingoUtilities
					.getAlphaColor(hueColor,
							(int) (255 * RibbonContextualTaskGroup.HUE_ALPHA)),
					0, bottomY, FlamingoUtilities.getAlphaColor(hueColor, 0));
			g2d.setPaint(paint);
			g2d.clip(outerContour);
			g2d.fillRect(0, topY, width, bottomY - topY + 1);
		}

		// paint outlines of the contextual task groups
		// paintContextualTaskGroupsOutlines(g);

		g2d.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#getTabButtonGap()
	 */
	@Override
	protected int getTabButtonGap() {
		return SubstanceSizeUtils.getAdjustedSize(SubstanceSizeUtils
				.getComponentFontSize(this.ribbon), super.getTabButtonGap(), 3,
				1, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#syncApplicationMenuRichTooltip
	 * ()
	 */
	@Override
	protected void syncApplicationMenuTips() {
		JRibbonRootPane ribbonRootPane = (JRibbonRootPane) SwingUtilities
				.getRootPane(this.ribbon);
		if (ribbonRootPane == null)
			return;
		SubstanceRibbonRootPaneUI ribbonRootPaneUI = (SubstanceRibbonRootPaneUI) ribbonRootPane
				.getUI();
		ribbonRootPaneUI.syncApplicationMenuTips();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jvnet.flamingo.ribbon.ui.BasicRibbonUI#paintMinimizedRibbonSeparator
	 * (java.awt.Graphics)
	 */
	@Override
	protected void paintMinimizedRibbonSeparator(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.translate(0, this.ribbon.getHeight() - 1);
		SeparatorPainterUtils.paintSeparator(this.ribbon, g2d, this.ribbon
				.getWidth(), 0, JSeparator.HORIZONTAL, false, 0);
		g2d.dispose();
	}
}

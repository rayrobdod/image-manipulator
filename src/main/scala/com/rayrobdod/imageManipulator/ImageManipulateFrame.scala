/*
	Copyright (c) 2012-2013, Raymond Dodge
	All rights reserved.
	 
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:
		* Redistributions of source code must retain the above copyright
		  notice, this list of conditions and the following disclaimer.
		* Redistributions in binary form must reproduce the above copyright
		  notice, this list of conditions and the following disclaimer in the
		  documentation and/or other materials provided with the distribution.
		* Neither the name "Image Manipulator" nor the names of its contributors
		  may be used to endorse or promote products derived from this software
		  without specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.rayrobdod.imageManipulator

import com.rayrobdod.swing.{ScalaSeqListModel, AbstractComboBoxModel}
import java.awt.{GridBagLayout, GridBagConstraints, Image}
import java.awt.event.{ActionListener, ActionEvent}
import java.awt.image.{BufferedImage, RenderedImage}
import javax.swing.{JLabel, JPanel, JButton, JComboBox, JFrame, ImageIcon}
import java.awt.image.BufferedImage.{TYPE_INT_ARGB => alpha}
import com.rayrobdod.swing.GridBagConstraintsFactory

/**
 * A frame that holds the common elements of the image conversions
 * 
 * @author Raymond Dodge
 * @since 2012 Jun 18-19
 * @version 2.0
 */
final class ImageManipulateFrame extends JFrame
{
	val model = new Observer(
		new FrameModel(
			new BufferedImage(5,5,alpha),
			new operations.Identity()
		)
	)
	
	model.addChangeListener{(m:FrameModel) =>
		originalImageLabel.setIcon(new ImageIcon(m.originalImageScaled))
		afterImageLabel.setIcon(new ImageIcon(m.afterImageScaled))
	}
	
	
	
	object SetupCustomModeArea extends ActionListener
	{
		def actionPerformed(e:ActionEvent)
		{
			modeCustomArea.removeAll
			
			val manipulation = modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex)
			manipulation.setup(modeCustomArea, UpdateFrameModelOperationActionListener)
			
			pack()
		}
	}
	
	object UpdateFrameModelOperationActionListener extends ActionListener {
		def actionPerformed(e:ActionEvent) {
			model.update(new FrameModel(
				model().originalImage,
				modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex).getImageOp
			))
		}
	}
	
	val originalImageLabel = new JLabel
	val afterImageLabel = new JLabel
	val loadButton = new JButton("Load")
	val saveButton = new JButton("Save")
	val modeChooser = new JComboBox[Operation](
		new ScalaSeqListModel(Operation.serviceSeq) with AbstractComboBoxModel[Operation]
	)
	val modeCustomArea = new JPanel
	
	modeChooser.setSelectedIndex(0)
	
	loadButton.addActionListener(SaveAndLoadListeners.loadListener{(a) => model.update(new FrameModel(a, model().operation))})
	saveButton.addActionListener(SaveAndLoadListeners.saveListener{() => model().afterImage})
	modeChooser.addActionListener(UpdateFrameModelOperationActionListener)
	modeChooser.addActionListener(SetupCustomModeArea)
	
	private val GridBagConstraintsRemainder = GridBagConstraintsFactory(gridwidth = GridBagConstraints.REMAINDER)
	
	setLayout(new GridBagLayout)
	add(originalImageLabel, new GridBagConstraints)
	add(afterImageLabel, GridBagConstraintsRemainder) 
	add(loadButton, new GridBagConstraints) 
	add(saveButton, GridBagConstraintsRemainder) 
	add(modeChooser, GridBagConstraintsRemainder) 
	add(modeCustomArea, GridBagConstraintsRemainder)
	
	setTitle("Image Manipulator")
	setIconImage(javax.imageio.ImageIO.read(this.getClass.getResource("frameIcon.png")))
}

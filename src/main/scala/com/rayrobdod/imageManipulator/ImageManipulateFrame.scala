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
 * @version 2012 Jun 18-19
 * @version 2012 Sept 09 - added 16x16 image icon to frame
 * @version 2012 Sept 10 - modified to use SaveAndLoadListeners
 * @version 2013 Feb 06 - GridBagConstraintsRemainder is now a val, not an object
 * @version 2013 Jun 05 - maked preview operations work on scaled instances
 * @version 2013 Jun 05 - private object scaleImage extends Function1 => private def scaleImage
 */
final class ImageManipulateFrame extends JFrame
{
	private def scaleImage(src:BufferedImage):BufferedImage = {
		if (src.getHeight > 400 && src.getHeight >= src.getWidth) {
			val width = src.getWidth * 400 / src.getHeight
			
			val a = src.getData.createCompatibleWritableRaster(width, 400)
			val b = new BufferedImage(src.getColorModel, a, true, new java.util.Hashtable())
			b.createGraphics().drawImage(src, 0, 0, width, 400, null)
			return b
		} else if (src.getWidth > 400 && src.getHeight <= src.getWidth) {
			val height = src.getHeight * 400 / src.getWidth
			
			val a = src.getData.createCompatibleWritableRaster(400, height)
			val b = new BufferedImage(src.getColorModel, a, true, new java.util.Hashtable())
			b.createGraphics().drawImage(src, 0, 0, 400, height, null)
			return b
		} else {
			src
		}
	}
	
	private var _originalImage:BufferedImage = new BufferedImage(5,5,alpha)
	private var _originalImageScaled:BufferedImage = _originalImage
	private var _afterImage:BufferedImage = _originalImage
	
	def originalImage = _originalImage
	def originalImageScaled = _originalImageScaled
	def originalImage_=(x:BufferedImage) =
	{
		_originalImage = x
		_originalImageScaled = scaleImage(originalImage)
		originalImageLabel.setIcon(new ImageIcon(originalImageScaled))
		pack()
	}
	
	def afterImage = {
		if (modeChooser.getSelectedIndex < 0) {
			originalImage
		} else {
			val manipulation = modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex)
			manipulation(originalImage)
		}
	}
	def afterImageScaled =  {
		if (modeChooser.getSelectedIndex < 0) {
			originalImageScaled
		} else {
			val manipulation = modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex)
			manipulation(originalImageScaled)
		}
	}
	
	object UpdateImageActionListener extends ActionListener
	{
		def actionPerformed(e:ActionEvent)
		{
			afterImageLabel.setIcon(new ImageIcon(afterImageScaled))
		}
	}
	
	object SetupCustomModeArea extends ActionListener
	{
		def actionPerformed(e:ActionEvent)
		{
			modeCustomArea.removeAll
			
			val manipulation = modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex)
			manipulation.setup(modeCustomArea, UpdateImageActionListener)
			
			pack()
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
	// add UpdateImageActionListener first so that it happens after the loadListener
	loadButton.addActionListener(UpdateImageActionListener)
	loadButton.addActionListener(SaveAndLoadListeners.loadListener(originalImage_=_))
	saveButton.addActionListener(SaveAndLoadListeners.saveListener(() => afterImage))
	modeChooser.addActionListener(UpdateImageActionListener)
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

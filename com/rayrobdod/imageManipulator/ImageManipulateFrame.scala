/*
	Copyright (c) 2012, Raymond Dodge
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
import java.awt.image.{BufferedImage, RenderedImage}
import javax.swing.{JLabel, JPanel, JButton, JComboBox, JFrame, ImageIcon}
import java.awt.image.BufferedImage.{TYPE_INT_ARGB => alpha}

/**
 * A frame that holds the common elements of the image conversions
 * 
 * @author Raymond Dodge
 * @version 2012 Jun 18-19
 * @version 2012 Sept 09 - added 16x16 image icon to frame
 * @todo make preview operations work on scaled instances
 */
class ImageManipulateFrame extends JFrame
{
	private object scaleImage extends Function1[BufferedImage,Image]
	{
		override def apply(src:BufferedImage):Image = {
			if (src.getHeight > 400)
				src.getScaledInstance(-1,400,0)
			else
				src
		}
	}
	
	private var _originalImage:BufferedImage = new BufferedImage(5,5,alpha)
	private var _afterImage:BufferedImage = _originalImage
	
	def originalImage = _originalImage
	def originalImage_=(x:BufferedImage) =
	{
		_originalImage = x
		originalImageLabel.setIcon(new ImageIcon(scaleImage(x)))
		afterImage = x
		pack()
	}
	
	def afterImage = _afterImage
	def afterImage_=(x:BufferedImage) =
	{
		_afterImage = x
		afterImageLabel.setIcon(new ImageIcon(scaleImage(x)))
	}
	
	object UpdateImageActionListener extends java.awt.event.ActionListener
	{
		def actionPerformed(arg0:java.awt.event.ActionEvent)
		{
			if (modeChooser.getSelectedIndex < 0)
			{
				afterImage = originalImage
			}
			else
			{
				val manipulation = modeChooser.getModel.getElementAt(modeChooser.getSelectedIndex)
				afterImage = manipulation(originalImage)
			}
		}
	}
	
	object SetupCustomModeArea extends java.awt.event.ActionListener
	{
		def actionPerformed(arg0:java.awt.event.ActionEvent)
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
	// add UpdateImageActionListener first so that it happens last
	loadButton.addActionListener(UpdateImageActionListener)
	loadButton.addActionListener(new LoadListener(originalImage_=_))
	saveButton.addActionListener(new SaveListener(() => afterImage))
	modeChooser.addActionListener(UpdateImageActionListener)
	modeChooser.addActionListener(SetupCustomModeArea)
	
	object GridBagConstraintsRemainder extends GridBagConstraints
	{
		gridwidth = GridBagConstraints.REMAINDER
	}
	
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

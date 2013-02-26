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
                                  
import java.beans.{PropertyChangeEvent, PropertyChangeListener}
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JPanel, JSlider, JFileChooser,
			JComboBox, JLabel, JCheckBox, JTextField}
import java.awt.{GridBagConstraints, GridBagLayout, GridLayout}
import javax.swing.event.{ChangeEvent, ChangeListener,
			DocumentEvent, DocumentListener}
import javax.swing.filechooser.FileFilter
import javax.imageio.ImageWriteParam
import javax.imageio.spi.ImageWriterSpi
import com.rayrobdod.swing.GridBagConstraintsFactory
import javax.swing.BorderFactory.{createEmptyBorder => EmptyBorder}
import javax.swing.SwingUtilities.{invokeLater => invokeOnAWT}

/**
 * A component that shows options useful for configuring an ImageWriteParam
 *
 * Currently only shows options for compression.
 * 
 * @author Raymond Dodge
 * @version 2012 Dec 27-28
 * @version 20123 Feb 24 CustomAccessory
 */
class ImageWriteParamAccessory(
		fileFiltersToSPI:Map[FileFilter, ImageWriterSpi]
) extends JPanel with PropertyChangeListener
{
	private var _p:Option[ImageWriteParam] = None;
	def p = _p;
	def p_=(p:Option[ImageWriteParam]) = {_p = p;
		CompressionAccessory.prepareParam(_p);
		CustomAccessory.prepareParam(_p);
	}
	
	object CompressionAccessory extends JPanel
	{
		object UseCompressionListener extends ActionListener {
			def actionPerformed(e:ActionEvent) = {
				p.foreach{(p2:ImageWriteParam) => 
					p2.setCompressionMode(if (useCompression.isSelected) {
							ImageWriteParam.MODE_EXPLICIT
						} else {
							ImageWriteParam.MODE_COPY_FROM_METADATA
						}
					)
					
					typeBox.removeAllItems()
					if (useCompression.isSelected) {
						// deal with Compression Types
						p2.setCompressionType(null)
						typeBox.setEnabled(p2.getCompressionTypes() != null)
						typeBox.addItem(null)
						Option(p2.getCompressionTypes).foreach{
							_.foreach{typeBox.addItem(_)}
						}
					} else {
						typeBox.setEnabled(false)
					}
				}
			}
		}
		
		val useCompression = new JCheckBox("Enable")
		useCompression.addActionListener(UseCompressionListener)
		
		val levelSlider = new JSlider(0, 2, 0)
		levelSlider.addChangeListener(new ChangeListener{
			def stateChanged(e:ChangeEvent) = {
				p.foreach{(p2:ImageWriteParam) => 
					if (p2.getCompressionMode == ImageWriteParam.MODE_EXPLICIT) { 
						if (p2.getCompressionType != null) {
							p2.setCompressionQuality(
								Option(p2.getCompressionQualityValues).map{
									_(levelSlider.getValue)
								}.getOrElse(0)
							)
						}
					}
				}
			}
		})
		
		val typeBox = new JComboBox[String]()
		typeBox.addActionListener(new ActionListener{
			def actionPerformed(e:ActionEvent) = {
				p.foreach{(p2:ImageWriteParam) =>
					if (p2.getCompressionMode == ImageWriteParam.MODE_EXPLICIT) { 
						p2.setCompressionType(
							Option(typeBox.getSelectedItem).map{_.toString}.orNull
						)
						
						if (p2.getCompressionType != null) {
							levelSlider.setEnabled(null != p2.getCompressionQualityValues)
							levelSlider.setMaximum( Option(p2.getCompressionQualityValues).map{_.size - 1}.getOrElse(0) )
							levelSlider.setValue(0)
						} else {
							levelSlider.setEnabled(false)
							levelSlider.setMaximum(0)
							levelSlider.setValue(0)
						}
					} else {
						levelSlider.setEnabled(false)
						levelSlider.setMaximum(0)
						levelSlider.setValue(0)
					}
				}
			}
		})
		
		this.setLayout(new java.awt.GridLayout(0,1))
		this.add(new JLabel("Compression:"))
		this.add(useCompression)
		this.add(typeBox)
		this.add(levelSlider)
		
		def prepareParam(p:Option[ImageWriteParam]) {
			p.map{(p2:ImageWriteParam) => 
				val shouldEnable = p2.canWriteCompressed
				
				useCompression.setEnabled(shouldEnable)
				useCompression.setSelected(false)
				if (shouldEnable)
					UseCompressionListener.actionPerformed(null)
			}
		}
	}
	CompressionAccessory.levelSlider.setEnabled(false)
	CompressionAccessory.typeBox.setEnabled(false)
	CompressionAccessory.useCompression.setEnabled(false)
	
	
	// WOO! REFLECTION
	object CustomAccessory extends JPanel
	{
		private val myLabel = new JLabel("Others:")
		CustomAccessory.this.add(myLabel)
		CustomAccessory.this.setLayout(new java.awt.GridLayout(0,1))
		
		def prepareParam(p:Option[ImageWriteParam]) {
			invokeOnAWT(new Runnable() { def run() {
			
				p.map{(p2:ImageWriteParam) => 
					CustomAccessory.this.removeAll()
					CustomAccessory.this.add(myLabel)
					
					val methods = p2.getClass.getMethods
					methods.filter{(m:java.lang.reflect.Method) =>
						// setters that are not included in the base classes
						m.getName.startsWith("set") &&
								m.getDeclaringClass() != classOf[javax.imageio.IIOParam] &&
								m.getDeclaringClass() != classOf[javax.imageio.ImageWriteParam]
					}.foreach{(m:java.lang.reflect.Method) =>
						val valueName = m.getName.drop(3)
						val STRING_TYPE = classOf[java.lang.String]
						
						if (1 == m.getParameterTypes.length) {
							m.getParameterTypes()(0) match {
								case java.lang.Boolean.TYPE => {
									val checkbox = new JCheckBox(valueName)
									
									CustomAccessory.this.add(checkbox)
									checkbox.addActionListener(new ActionListener{
										def actionPerformed(e:ActionEvent) {
											m.invoke(p2, checkbox.isSelected:java.lang.Boolean);
										}
									})
									
									try {
										val setValue = p2.getClass.getMethod("is" + valueName).invoke(p2);
										
										checkbox.setSelected(setValue match {
											case x:java.lang.Boolean => x
										})
										
										
									} catch {
										case e:NoSuchMethodException => { 
											try {
												val setValue = p2.getClass.getMethod("get" + valueName).invoke(p2);
												
												checkbox.setSelected(setValue match {
													case x:java.lang.Boolean => x
												})
												
											} catch {
												case e2:NoSuchMethodException => {
													m.invoke(p2, false:java.lang.Boolean);
												}
											}
										}
									}
								}
								case STRING_TYPE => {
									val label = new JLabel(valueName + ": ")
									val textbox = new JTextField(15)
									
									CustomAccessory.this.add({
										val p = new JPanel(new GridLayout(0,1))
										p.add(label)
										p.add(textbox)
										p
									})
									
									textbox.getDocument.addDocumentListener(new DocumentListener{
										def changedUpdate(e:DocumentEvent) {
											m.invoke(p2, textbox.getText);
										}
										def insertUpdate(e:DocumentEvent) {
											m.invoke(p2, textbox.getText);
										}
										def removeUpdate(e:DocumentEvent) {
											m.invoke(p2, textbox.getText);
										}
									})
									
									try {
										val setValue = p2.getClass.getMethod("get" + valueName).invoke(p2);
										
										textbox.setText(setValue.toString)
										
									} catch {
										case e2:NoSuchMethodException => {
											m.invoke(p2, "");
										}
									}
								}
								case _ => {}
							}
						}
					}
				}
				
				ImageWriteParamAccessory.this.validate()
			}})
		}
	}
	
	private val partGridBag = GridBagConstraintsFactory(
		gridwidth = GridBagConstraints.REMAINDER,
		weightx = 1,
		fill = GridBagConstraints.BOTH
	)
	
	this.setLayout(new GridBagLayout)
	this.add(CompressionAccessory, partGridBag)
	this.add(CustomAccessory, partGridBag)
	this.setAlignmentY(1)
	

	
	
	def propertyChange(e:PropertyChangeEvent) {
		val prop = e.getPropertyName();
		
		if (JFileChooser.FILE_FILTER_CHANGED_PROPERTY.equals(prop)) {
			e.getNewValue match {
				case x:FileFilter => this.p = (fileFiltersToSPI.lift(x).map{_.createWriterInstance.getDefaultWriteParam});
				
				// if null, then the All Files file filter is being used, which only
				// shows up in the Open File dialog. Thus, the Save Dialog thing has ended,
				// and this no longer needs to care.
				// Attempting to avert a memory leak here.
				case null => e.getSource() match {
					case y:JFileChooser => y.removePropertyChangeListener(this)  
				}
			}
		}
	}
}


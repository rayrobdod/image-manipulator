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
package com.rayrobdod.swing

import javax.swing.event.{ListSelectionListener, ListSelectionEvent}
import javax.swing.{ListModel, AbstractListModel, ComboBoxModel}

/** 
 * A ListModel backed by a Scala Seq.
 * @author Raymond Dodge
 * @version 01 Feb 2012
 * @version 19 Jun 2012 - moving from com.rayrobdod.deductionTactics.test to com.rayrobdod.swing
 */
class ScalaSeqListModel[A](backing:Seq[A]) extends AbstractListModel[A]
{
	def getSize = backing.size
	def getElementAt(i:Int) = backing(i)
}

/** 
 * A ListModel with getSize elements, one each from 1 to getSize.
 * @author Raymond Dodge
 * @version 01 Feb 2012
 * @version 19 Jun 2012 - moving from com.rayrobdod.deductionTactics.test to com.rayrobdod.swing
 * @version 09 Nov 2012 - apparently this needed to be templated on an Integer, not an int.
 */
class RangeListModel(override val getSize:Int) extends AbstractListModel[java.lang.Integer]
{
	/** returns the parameter */
	override def getElementAt(i:Int):java.lang.Integer = i
}

/**
 * @author Raymond Dodge
 * @version 19 Jun 2012
 */
trait AbstractComboBoxModel[E] extends ComboBoxModel[E]
{
	private var selectedItem:Object = ""
	
	def getSelectedItem() = selectedItem
	def setSelectedItem(x:Object) = {selectedItem = x}
}

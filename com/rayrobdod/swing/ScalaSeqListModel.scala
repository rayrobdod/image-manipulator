package com.rayrobdod.swing

import javax.swing.event.{ListSelectionListener, ListSelectionEvent}
import javax.swing.{ListModel, AbstractListModel, ComboBoxModel}

/** 
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
 * @author Raymond Dodge
 * @version 01 Feb 2012
 * @version 19 Jun 2012 - moving from com.rayrobdod.deductionTactics.test to com.rayrobdod.swing
 * @version 09 Nov 2012 - apparently this needed to be templated on an Integer, not an int.
 */
class RangeListModel(override val getSize:Int) extends AbstractListModel[java.lang.Integer]
{
	override def getElementAt(i:Int):Integer = i
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

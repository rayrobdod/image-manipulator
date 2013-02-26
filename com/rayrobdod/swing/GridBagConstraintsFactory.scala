package com.rayrobdod.swing

import java.awt.{GridBagConstraints, Insets}

/**
 * A factory for GridBagConstraints.
 * 
 * This uses the new method of GridBagConstraints; the main difference is that this has default parameters.
 * 
 * @version 2013 Jan 29
 */
object GridBagConstraintsFactory {
	def apply(
			gridx:Int = GridBagConstraints.RELATIVE,
			gridy:Int = GridBagConstraints.RELATIVE,
			gridwidth:Int = 1,
			gridheight:Int = 1,
			weightx:Double = 0,
			weighty:Double = 0,
			anchor:Int = GridBagConstraints.CENTER,
			fill:Int = GridBagConstraints.NONE,
			insets:Insets = new Insets(0,0,0,0),
			ipadx:Int = 0,
			ipady:Int = 0
	) = new GridBagConstraints(
			gridx, gridy, gridwidth, gridheight,
			weightx, weighty, anchor, fill, insets, ipadx, ipady
	)
}

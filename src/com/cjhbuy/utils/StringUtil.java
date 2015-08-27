/**
 * 
 */
package com.cjhbuy.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @author ps
 *
 */
public class StringUtil {
	/** 
     * 使用BigDecimal，保留小数点后两位 
     */  
    public static String format(double value) {  
  
        BigDecimal bd = new BigDecimal(value);  
        bd = bd.setScale(2, RoundingMode.HALF_UP);  
        return bd.toString();  
    } 
}

package com.gclasscn.base.demo

import java.io.File
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import com.gclasscn.base.domain.User

class Demo {
	
	companion object {
		@JvmStatic fun main(args: Array<String>) {
			var list = arrayListOf(1,2,3)
			var result1 = Demo().max(list, fun(a: Int, b: Int): Boolean{return a < b})
			var result2 = Demo().max(list, {a, b -> a < b})
			var result3 = Demo().max(list){ a, b -> a < b }
			var result4 = Demo().max(list, fun(a: Int, b: Int) = a < b)
			var result5 = Demo().max(list, fun(a: Int, b: Int): Boolean = a < b)
			println(result1)
			println(result2)
			println(result3)
			println(result4)
			println(result5)
		}
	}
	
	fun <T> max(collection: Collection<T>, less: (T, T) -> Boolean): T? {
	    var max: T? = null
	    for (it in collection)
	        if (max == null || less(max, it))
	            max = it
	    return max
	}
	
	/**
	 * 尾递归函数
	 */
	tailrec fun fixed(x: Double = 1.0) : Double = if(x == Math.cos(x)) x else fixed(Math.cos(x))
	
	/**
	 * 字符串拼接
	 * 声明可变参数: vararg
	 */
	fun appendStr(): String? {
		val hour = 10
		val minute = 20
		val second = 30
		return "$hour:$minute:$second"
	}
	
	/**
	 * when用法
	 */
	fun switch(param: Any) = when(param){
		is Number -> "数字类型"
		is String -> "字符串类型"
		else -> "未知类型"
	}
	
	/**
	 * 检测范围[0, 10]
	 */
	//fun range(number: Int): Boolean{
	//	return number in 0..10
	//}
	fun range(number: Int) = number in 0..10
	
	/**
	 * 区间迭代: i范围[0, 10]
	 */
	fun loop(){
		for(i in 0..10){
			println(i)
		}
	}
	
	fun Any?.toString(): String {
	    if (this == null) return "null"
	    // 空检测之后，“this”会自动转换为非空类型，所以下面的 toString()
	    // 解析为 Any 类的成员函数
	    return toString()
	}
	
	fun <T, R> List<T>.map(transform: (T) -> R): List<R> {
	    val result = arrayListOf<R>()
	    for (item in this)
	        result.add(transform(item))
	    return result
	}
	
}

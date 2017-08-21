package com.gclasscn.base.demo

import java.util.ArrayList

var list = ArrayList<String>()

/**
 * 操作符重载
 */
operator fun String.unaryPlus(){
	list.add(this)
}

fun main(args: Array<String>) {
	var names = listOf("诸葛建国", "西门富贵", "慕容广场", "司马鞭")
	names.forEach { + it }
 	list.forEach { println(it) }		
}
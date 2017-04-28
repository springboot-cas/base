$(function () {
	"use strict";

	var contextPath = $("#context-path").prop("content");
	
	$(".file").addClass("active").siblings().removeClass("active");
	
    /**
     * 查询在线用户列表
     */
	var pageNum = 1;
	var size = 10;
	var query = $("#search-input").val();
	listFiles();
	function listFiles(){
		$.ajax({
			type: "GET",
			url: contextPath + "files",
			data: {
				pageNum: pageNum,
				size: size,
				query: query
			},
			success: function(pageList){
				$("#fileContent").empty();
				$("#fileTmpl").tmpl({files: pageList.data}).appendTo("#fileContent");
				pagination(pageList.totalPages);
			}
		});
	}
	
	/**
     * 点击搜索
     */
    $("#search-btn").click(function(){
    	query = $("#search-input").val();
		pageNum = 1;
		listFiles();
    });
    
    /**
     * 回车搜索
     */
    $("#search-input").keydown(function(event){
    	var e = event || window.event || arguments.callee.caller.arguments[0];
    	if(e && e.keyCode == 13){
    		e.preventDefault();
    		query = $(this).val();
    		pageNum = 1;
    		listFiles();
    	}
    });
	
	/**
     * 分页事件
     */
    function pagination(totalPages){
    	if(totalPages > 1) {
    		$(".pagination").show();
    		$(".pagination").bootstrapPaginator({
        		bootstrapMajorVersion: 3.0,
        		currentPage: pageNum,
        		totalPages: totalPages,
        		numberOfPages: 10,
        		itemTexts: function (type, page, currentpage) {
                    switch (type) {
        	            case "first": return "首页";
        	            case "prev" : return "上一页";
        	            case "next" : return "下一页";
        	            case "last" : return "尾页";
        	            case "page" : return page;
                    }
                },
                onPageClicked: function(event, originalEvent, type, current){
        			if(pageNum == current){
        				return false;
        			}
        			pageNum = current;
        			listFiles();
        		}
        	});
    	}else{
    		$(".pagination").hide();
    	}
    	
    	$(".pagination").find("li a").prop("title", "");
    };
    
    /**
     * 下载备份
     */
    $("#fileContent").on("click", "tr td .j-download", function(){
    	var filename = $(this).parent().siblings(".filename").html();
    	window.location.href = contextPath + "files/name?name=" + encodeURIComponent(filename);
    });
    
    /**
     * 删除备份
     */
    $("#fileContent").on("click", "tr td .j-remove", function(){
    	var filename = $(this).parent().siblings(".filename").html();
    	layer.confirm("确定删除?", {title: '删除备份'}, function (index) {
    		layer.close(index);
    		$.ajax({
        		type: "POST",
        		url: contextPath + "files/name",
        		data: {
        			_method: "DELETE",
        			name: filename
        		},
        		success: function(){
        			pageNum = 1;
            		listFiles();
        		}
        	});
        });
    });
    
    /**
     * 上传备份
     */
    $("#file").change(function(){
    	var formData = new FormData();
        formData.append("file", $(this).prop("files")[0]);
        $.ajax({
            url: contextPath + "files",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false
        });
    });
});
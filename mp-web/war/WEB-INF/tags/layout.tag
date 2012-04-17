<%@ tag%>
<%@ attribute name="navButton" type="java.lang.String"
	rtexprvalue="true" required="true"
	description="Selected button in navigation menu"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>License Manager</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<!-- Le styles -->
<link href="/css/bootstrap.min.css" rel="stylesheet">
<style>
body {
	padding-top: 60px;
	/* 60px to make the container go all the way to the bottom of the topbar */
}
</style>

<link href="/css/bootstrap-responsive.css" rel="stylesheet">

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
</head>

<body>
	<!-- MENU -->
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="/web/index">License manager</a>
				<div class="nav-collapse">
					<ul class="nav">
						<li class="active"><a href="/web/index">Home</a></li>
						<li><a href="/web/about">About</a></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span3">
				<div class="well sidebar-nav">
					<ul class="nav nav-list">
						<li class="nav-header">Actions</li>
						<li<c:if test="${navButton=='overview'}"> class="active"</c:if>><a
							href="/web/index">Overview</a></li>
						<li<c:if test="${navButton=='licenses'}"> class="active"</c:if>><a href="/web/licenses">Issued licenses</a></li>
						<li><a href="#">Link</a></li>
					</ul>
				</div>
				<!--/.well -->
			</div>
			<!--/span-->
			<div class="span9">
				<jsp:doBody />
			</div>
			<!--/span-->
		</div>
		<!--/row-->

		<hr>

		<footer>
			<p>Icons from <a href="http://glyphicons.com">Glyphicons Free</a>, licensed under <a href="http://creativecommons.org/licenses/by/3.0/">CC BY 3.0</a>.</p>
		</footer>

	</div>
	<!--/.fluid-container-->

	<!-- Le javascript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script src="/js/bootstrap.min.js"></script>
</body>
</html>


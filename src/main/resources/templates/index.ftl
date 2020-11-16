<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link rel="stylesheet" href="/style/login.css">
    <#--<link rel="icon" type="image/x-icon" href="framework/img/smallIcon.png">-->
    <title>欢迎使用盐城市水利信息管理系统</title>
</head>
<body>
<header>
    <#--<img id="logo" src="${(theme.BIG_ICO)?default("/img/logo.png")}" alt="苏易采"/>-->
</header>
<script type="text/javascript">
    function post(url, params) {
        var temp = document.createElement("form"); //创建form表单
        temp.action = url;
        temp.method = "post";
        temp.style.display = "none";//表单样式为隐藏
        for (var item in params) {//初始化表单内部的控件
            //根据实际情况创建不同的标签元素
            var opt = document.createElement("input");  //添加input标签
            opt.type = "text";   //类型为text
            opt.id = item;      //设置id属性
            opt.name = item;    //设置name属性
            opt.value = params[item];   //设置value属性
            temp.appendChild(opt);
        }

        document.body.appendChild(temp);
        temp.submit();
        return temp;
    }
</script>
<div id="content">
    <div class="center">
        <form action="/save.do" method="post">
            <div id="login">
                <div class="form">
                    <div class="right">
                        <div class="input-wrap">
                            <img class="user-img" src="/img/ic1.png">
                            <input class="input" type="text" name="sql" placeholder="请输入sql">
                        </div>
                        <div class="input-wrap">
                            <img class="user-img" src="/img/ic2a.png">
                            <input class="input" type="text" name="filePath" placeholder="请输入文件保存路径，例如：F:/file">
                        </div>
                        <div class="input-wrap">
                            <img class="user-img" src="/img/ic2b.png">
                            <input class="input" type="text" name="fileName" placeholder="请输入文件名，例如：excelFile">
                        </div>
                        <div class="input-wrap">
                            <img class="user-img" src="/img/icon_ai.png">
                            <input class="input" type="text" name="suffix" placeholder="请输入文件后缀，例如：xlsx">
                        </div>
                        <span id="error">${error!""}</span>
                        <button type="submit">生成文件</button>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<script>
    var initResult =
    ${(initResult)?default("1")}
</script>
<script src="${(jsPath)?default("1")}"></script>
<#--<footer>盐城工学院（C）2019 保留所有权利。</footer>-->
</body>
</html>
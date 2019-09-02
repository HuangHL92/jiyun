
$(function() {
    validateRule();
	$('.imgcode').click(function() {
		var url = ctx + "captcha/captchaImage?type=" + captchaType + "&s=" + Math.random();
		$(".imgcode").attr("src", url);
	});
});

$.validator.setDefaults({
    submitHandler: function() {
		login();
    }
});

function login() {
	$.modal.loading($("#btnSubmit").data("loading"));
	var username = $.common.trim($("input[name='username']").val());
    var password = $.common.trim($("input[name='password']").val());
    var validateCode = $("input[name='validateCode']").val();
    var rememberMe = $("input[name='rememberme']").is(':checked');
    var key = $.common.trim($("input[name='key']").val());

    var $key = CryptoJS.enc.Utf8.parse(key);
    password = CryptoJS.enc.Utf8.parse(password);
    var encrypted = CryptoJS.AES.encrypt(password, $key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7});
    var encryptedPwd = encrypted.toString();
    //密码字段赋值
    $("input[name='pwd']").val(encryptedPwd);

    $.ajax({
        type: "post",
        url: ctx + "login",
        data: {
            "username": username,
            "pwd": encryptedPwd,
            "validateCode" : validateCode,
            "rememberMe": rememberMe,
            "key": key
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(headerName, token);
        },
        success: function(r) {
            if (r.code == 0) {
                location.href = ctx + 'index';
            } else {
                $.modal.closeLoading();
                $('.imgcode').click();
                $(".code").val("");
                if (r.code == 4001) {
                    layer.alert(r.msg, {
                        icon: $.modal.icon(modal_status.WARNING),
                        title: "系统提示",
                        btn: ['确认'],
                        btnclass: ['btn btn-primary'],
                    }, function (index) {
                        location.href = ctx + 'reset?username=' + username;
                    });

                    // $.modal.alert(r.msg, modal_status.WARNING);
                } else {
                    $.modal.msg(r.msg);
                }
            }
        }
    });
}

function validateRule() {
    var icon = "<i class='fa fa-times-circle'></i> ";
    $("#signupForm").validate({
        rules: {
            username: {
                required: true
            },
            password: {
                required: true
            }
        },
        messages: {
            username: {
                required: icon + "请输入您的用户名",
            },
            password: {
                required: icon + "请输入您的密码",
            }
        }
    })
}

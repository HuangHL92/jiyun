
$(function() {
    validateRule();

    //返回按钮
    $("#gobackBtn").click(function(){
        location.href = "/home/login";
    });

});


function reset() {

	$.modal.loading($("#submitBtn").data("loading"));
	var username = $.common.trim($("input[name='username']").val());
    var password = $.common.trim($("input[name='password']").val());
    var newPassword = $.common.trim($("input[name='newPassword']").val());
    var key = $.common.trim($("input[name='key']").val());

    var $key = CryptoJS.enc.Utf8.parse(key);
    $.ajax({
        type: "post",
        url: ctx + "reset",
        data: {
            "username": username,
            "password": encrypt($key, password),
            "newPassword": encrypt($key, newPassword),
            "key": key
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(headerName, token);
        },
        success: function(r) {
            $.modal.closeLoading();
            if (r.code == 0) {
                layer.alert(r.msg, {
                    icon: $.modal.icon(modal_status.success),
                    title: "系统提示",
                    btn: ['确认'],
                    btnclass: ['btn btn-primary'],
                }, function (index) {
                    location.href = ctx + 'login';
                });
            } else {
                $.modal.msg(r.msg);
            }
        }
    });
}

function encrypt(key, target) {
    target = CryptoJS.enc.Utf8.parse(target);
    return CryptoJS.AES.encrypt(target, key, {mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7}).toString();
}

function validateRule() {
    $("#resetForm").validate({
        errorElement: 'div',
        errorClass: 'password-tips-error',
        errorPlacement: function(error, element) {
            error.insertAfter($('#errorInsertPoint'));
        },
        rules: {
            username: {
                required: true
            },
            password: {
                required: true,
                minlength: 6,
                maxlength: 16
            },
            newPassword: {
                required: true,
                minlength: 6,
                maxlength: 16
            },
            reNewPassword: {
                required: true,
                equalTo: "#newPassword"
            }
        },
        messages: {
            username: {
                required: "请输入您的用户名",
            },
            password: {
                required: "请输入您的原密码",
                minlength: "密码不能小于6个字符",
                maxlength: "密码不能大于16个字符"
            },
            newPassword: {
                required: "请输入新密码",
                minlength: "密码不能小于6个字符",
                maxlength: "密码不能大于16个字符"
            },
            reNewPassword: {
                required: "请再次输入新密码",
                equalTo: "两次密码输入不一致"
            }
        },
        submitHandler: function() {
            reset();
        }
    })
}

// 存放主要的交互逻辑js代码
// javascript 模块化，特别的注意
var seckill = {
    // 封装秒杀相关的ajax地址url，方便维护修改
    URL: {
        now: function () {  // 封装url参数
            return '/seckill/time/now';
        },
        exposer: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckill/' + seckillId + "/" + md5 + "/execution";
        }
    },
    // 验证手机号
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    // 秒杀处理回调
    handlerSeckill: function (seckillId, node) {
        // 时间显示及秒杀
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        // 发送post请求
        $.post(seckill.URL.exposer(seckillId), {}, function (result) {
            // 在此回调中执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    // 开始秒杀
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log("killUrl=" + killUrl);

                    // 绑定一次点击事件,防止同一时间多次操作
                    $('#killBtn').one('click', function () {
                        // 先禁用按钮
                        $(this).addClass('disabled');
                        // 发送秒杀请求,执行秒杀
                        $.post(killUrl, {}, function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                // 显示秒杀结果
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            }

                        });
                    });
                    node.show();
                } else {
                    // 未开启
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    // 重新计算计时逻辑
                    seckill.countdown(seckillId, now, start, end);
                }
            } else {
                console.log('result = ' + result);
            }
        });
    },
    // 封装成函数，秒杀计时(方便重用及逻辑分开)
    countdown: function (seckillId, nowTime, startTime, endTime) {
        // 获取id显示计时
        var seckillBox = $('#seckill-box');
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            // 秒杀未开始，开始倒计时,计时事件绑定
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                // 时间格式
                var format = event.strftime('开始倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () { // 计时完成时执行此回调
                // 需要用户id和节点存放结果或者按钮
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },
    // 详情页秒杀逻辑
    detail: {
        init: function (params) {
            // 手机验证和登录，计时交互
            // 规划交互流程
            // 从cookie中查找手机号
            var killPhone = $.cookie('killPhone');

            // 验证手机号
            if (!seckill.validatePhone(killPhone)) {
                // 绑定手机号
                var killPhoneModal = $('#killPhoneModal');
                // 控制输出，显示弹出层
                killPhoneModal.modal({
                    show: true, // 显示弹出层
                    backdrop: 'static', // 禁止位置关闭，点击其他地方弹出层不消失
                    keyboard: false // 关闭键盘事件
                });
                // 添加事件监听
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    // 判断手机号
                    if (seckill.validatePhone(inputPhone)) {
                        // 是手机号则写入cookie中，其中第三个参数是制定（指定有效时间：expires7天，路径则表示在此路径中有效，其他则无效）
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        // 刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！！！</label>').show(200); // 在200毫秒后显示
                    }
                });
            }
            // 验证通过，或已经登录
            // 计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    // 时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log("回调没有数据 = " + result);
                }
            });
        }
    }
}


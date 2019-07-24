//页面控件初始化入口方法
function initMap() {
    //折叠控件初始化
    $("[data-toggle='map']").each(function () {
        var m=new ShowMap($(this))
    })
}
var ShowMap = function (input)
{
    var  id=input.data("id")||'allmap'//地图标注id
        ,city=input.data("city")||'上海市'//中心城市
        ,markType=input.data("markType")//地图标注类型：(markPoint)标点、(pointGroup)标点集合
        ,lng=input.data("longitudeid")//保存经度属性id（标点用）
        ,lat=input.data("latitudeid")//保存纬度属性id（标点用）
        ,makerId=input.data("makerid")||''//定位控件id（标点用）
        ,isShow=input.data("isshow")||false //是否显示用，默认false
        ,enableScroll=input.data("enablescroll")||false //启用滚轮放大缩小（默认false）
    // 百度地图控件
    var recordmap = new BMap.Map(id);  // 创建Map实例
    var point;          // 创建中心点
    var zoom;           // 地图缩放比例

    // 标点的场合
    if(markType=="markPoint")
    {
        if (makerId != '') {
            mapMaker = makerId;
        }
        if ($('#'+lng).val() === '' && $('#'+lat).val() === '') {
            point = city;
            zoom = 12;
        } else {
            point = new BMap.Point($('#'+lng).val(), $('#'+lat).val());
            zoom = 15;
            var marker1 = new BMap.Marker(point);               // 创建标注
            recordmap.addOverlay(marker1);                      // 将标注添加到地图中
            if (!isShow) {
                marker1.enableDragging();  //设置可拖拽
                marker1.addEventListener("dragend", function (e) {  //拖动事件
                    $('#'+lng).val(e.point.lng);
                    $('#'+lat).val(e.point.lat);
                });
            }
        }
        recordmap.centerAndZoom(point, zoom);                     // 初始化地图,设置中心点坐标和地图级别。

        if (!isShow) {
            recordmap.addEventListener("click", function (e) {
                $('#'+lng).val(e.point.lng);
                $('#'+lat).val(e.point.lat);
                recordmap.clearOverlays();
                var marker1 = new BMap.Marker(e.point);         // 创建标注
                recordmap.addOverlay(marker1);                   // 将标注添加到地图中
                marker1.enableDragging();                        //设置可拖拽
                marker1.addEventListener("dragend", function (e) {  //拖动事件
                    $('#'+lng).val(e.point.lng);
                    $('#'+lat).val(e.point.lat);
                });
            });
            // 绑定点击事件，触发时搜索目标地址并创建Marker
            if (makerId != "") {
                $('#'+mapMaker).on("click", function ()
                {
                    // 创建地址解析器实例
                    var myGeo = new BMap.Geocoder();
                    // 将地址解析结果显示在地图上,并调整地图视野
                    var address = $('#'+mapMaker).parent().prev().children("input").val();
                    myGeo.getPoint(address, function (point) {
                        if (point) {
                            $('#'+lng).val(point.lng);
                            $('#'+lat).val(point.lat);
                            recordmap.clearOverlays();
                            recordmap.centerAndZoom(point, 16);
                            recordmap.addOverlay(new BMap.Marker(point));
                        } else {
                            layer.msg('您填写的地址没有解析到结果!');
                        }
                    }, city);
                });
                recordmap.addEventListener("load", function () {
                    $('#'+mapMaker).click();
                });
            }
        }
    }

    if(enableScroll)
    {
        recordmap.enableScrollWheelZoom();                      //启用滚轮放大缩小
    }
    var top_left_control = new BMap.ScaleControl({
        anchor: BMAP_ANCHOR_TOP_LEFT});// 左上角，添加比例尺
    var top_left_navigation = new BMap.NavigationControl();  //左上角，添加默认缩放平移控件
    var top_right_navigation = new BMap.NavigationControl({
        anchor: BMAP_ANCHOR_TOP_RIGHT, type: BMAP_NAVIGATION_CONTROL_SMALL}); //右上角，仅包含平移和缩放按钮
    recordmap.addControl(top_left_control);
    recordmap.addControl(top_left_navigation);
    recordmap.addControl(top_right_navigation);

}





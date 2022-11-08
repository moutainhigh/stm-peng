/**
 * 单位转换工具类
 * Created by huangping on 2017/6/27.
 */
+function ($) {
    //-- 单位转换方法开始 --
    function unitTransform(value, unit) {
        var str;
        var valueTemp;
        if (null == value) {
            return '--';
        }
        if (isNaN(value)) {
            valueTemp = new Number($.trim(value));
        } else {
            valueTemp = new Number(value);
        }

        if (isNaN(valueTemp)) {
            return value + unit;
        }
        switch (unit) {
            case "ms":
            case "毫秒":
                str = transformMillisecond(valueTemp);
                break;
            case "s":
            case "秒":
                str = transformSecond(valueTemp);
                break;
            case "Bps":
                str = transformByteps(valueTemp);
                break;
            case "bps":
            case "比特/秒":
                str = transformBitps(valueTemp);
                break;
            case "KB/s":
                str = transformKBs(valueTemp);
                break;
            case "Byte":
                str = transformByte(valueTemp);
                break;
            case "KB":
                str = transformKB(valueTemp);
                break;
            case "MB":
                str = transformMb(valueTemp);
                break;
            default:
                str = value + unit;
                break;
        }
        return str;

    }

    function transformMillisecond(milliseconds) {
        var millisecond = milliseconds;
        var seconds = 0, second = 0;
        var minutes = 0, minute = 0;
        var hours = 0, hour = 0;
        var day = 0;
        if (milliseconds > 1000) {
            millisecond = milliseconds % 1000;
            second = seconds = (milliseconds - millisecond) / 1000;
        }
        if (seconds > 60) {
            second = seconds % 60;
            minute = minutes = (seconds - second) / 60;
        }
        if (minutes > 60) {
            minute = minutes % 60;
            hour = hours = (minutes - minute) / 60;
        }
        if (hours > 24) {
            hour = hours % 24;
            day = (hours - hour) / 24;
        }
        var sb = "";
        sb = day > 0 ? (sb += (day + "天")) : sb;
        sb = hour > 0 ? (sb += (hour + "小时")) : sb;
        sb = minute > 0 ? (sb += (minute + "分")) : sb;
        sb = second > 0 ? (sb += (second + "秒")) : sb;
        sb = millisecond > 0 ? (sb += (millisecond + "毫秒")) : sb;
        sb = "" == sb ? (sb += (millisecond + "毫秒")) : sb;
        return sb;
    }

    function transformSecond(seconds) {
        var second = seconds;
        var minutes = 0, minute = 0;
        var hours = 0, hour = 0;
        var day = 0;
        if (seconds > 60) {
            second = seconds % 60;
            minute = minutes = (seconds - second) / 60;
        }
        if (minutes > 60) {
            minute = minutes % 60;
            hour = hours = (minutes - minute) / 60;
        }
        if (hours > 24) {
            hour = hours % 24;
            day = (hours - hour) / 24;
        }
        var sb = "";
        sb = day > 0 ? (sb += (day + "天")) : sb;
        sb = hour > 0 ? (sb += (hour + "小时")) : sb;
        sb = minute > 0 ? (sb += (minute + "分")) : sb;
        sb = second > 0 ? (sb += (second + "秒")) : sb;
        sb = "" == sb.toString() ? (sb += (second + "秒")) : sb;
        return sb;
    }

    function transformByteps(bpsNum) {
        var sb = "";
        var precision = 2;
        if (bpsNum > 1000 * 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "GBps";
        } else if (bpsNum > 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000)).toFixed(precision) + "MBps";
        } else if (bpsNum > 1000) {
            sb += (bpsNum / 1000).toFixed(precision) + "KBps";
        } else {
            sb += bpsNum.toFixed(precision) + "Bps";
        }
        return sb;
    }

    function transformBitps(bpsNum) {
        var sb = "";
        var precision = 2;
        if (bpsNum > 1000 * 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000 * 1000)).toFixed(precision) + "Gbps";
        } else if (bpsNum > 1000 * 1000) {
            sb += (bpsNum / (1000 * 1000)).toFixed(precision) + "Mbps";
        } else if (bpsNum > 1000) {
            sb += (bpsNum / 1000).toFixed(precision) + "Kbps";
        } else {
            sb += bpsNum.toFixed(precision) + "bps";
        }
        return sb;
    }

    function transformKBs(KBsNum) {
        var sb = "";
        var precision = 2;
        if (KBsNum > 1024 * 1024) {
            sb += (KBsNum / (1024 * 1024)).toFixed(precision) + "GB/s";
        } else if (KBsNum > 1024) {
            sb += (KBsNum / 1024).toFixed(precision) + "MB/s";
        } else {
            sb += KBsNum.toFixed(precision) + "KB/s";
        }
        return sb;
    }

    function transformByte(byteNum) {
        var sb = "";
        var precision = 2;

        if (byteNum > 1024 * 1024 * 1024) {
            sb += (byteNum / (1024 * 1024 * 1024)).toFixed(precision) + "GB";
        } else if (byteNum > 1024 * 1024) {
            sb += (byteNum / (1024 * 1024)).toFixed(precision) + "MB";
        } else if (byteNum > 1024) {
            sb += (byteNum / 1024).toFixed(precision) + "KB";
        } else {
            sb += byteNum.toFixed(precision) + "Byte";
        }
        return sb;
    }

    function transformKB(KBNum) {
        var sb = "";
        var precision = 2;

        if (KBNum > 1024 * 1024) {
            sb += (KBNum / (1024 * 1024)).toFixed(precision) + "GB";
        } else if (KBNum > 1024) {
            sb += (KBNum / 1024).toFixed(precision) + "MB";
        } else {
            sb += KBNum.toFixed(precision) + "KB";
        }
        return sb;
    }

    function transformMb(mbNum) {
        var sb = "";
        var precision = 2;

        if (mbNum > 1024) {
            sb += (mbNum / 1024).toFixed(precision) + "GB";
        } else {
            sb += mbNum.toFixed(precision) + "MB";
        }
        return sb;
    }

    //-- 单位转换方法结束 --

    oc.ns('oc.module.topo');
    oc.module.topo.unitTransform = unitTransform;
}(jQuery);
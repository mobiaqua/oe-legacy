require busybox_1.1x.inc

#MobiAqua: added not_include_on_os_x.patch
SRC_URI += "file://fix-iptunnel-location.patch \
	    file://not_include_on_os_x.patch \
	    file://fix-PRIO_PROCESS.patch \
	"

PR = "${INC_PR}.0"

SRC_URI[md5sum] = "96dd43cc7cee4017a6bf31b7da82a1f5"
SRC_URI[sha256sum] = "10954fcd5c48d8a262a3497b16227bf983a05658bf2bf661af2fdeca773f2fc0"

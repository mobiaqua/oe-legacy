UBOOT_MACHINE = "omap4_panda_config"
DESCRIPTION = "U-Boot - the Universal Boot Loader"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PRIORITY = "optional"
LICENSE = "GPLv2"
PROVIDES += "u-boot x-load"

DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_board-tv = "10"

COMPATIBLE_MACHINE = "board-tv"

SRCREV = "36fec02b1f90b92cf51ec531564f9284eae27ab4"

PV = "v2020.04"
PE = "1"

FILESPATHPKG =. "files/igepv5:files:"

SRC_URI = "git://gitlab.denx.de/u-boot.git;protocol=git;branch=${PV} \
	   file://Kconfig \
	   file://Makefile \
	   file://board.c \
	   file://board_configuration.c \
	   file://board_configuration.h \
	   file://omap5_igep0050_defconfig \
	   file://omap5_igep0050.h \
	   file://mux_data.h \
	   file://smsc75xx.c \
	   file://boot-nfs.script \
	   file://boot-nfs2.script \
	   file://boot-sdcard.script \
	   file://wa-makefile-issue.patch \
	   file://older-gcc.patch \
	   file://0001-Add-linux-compiler-gcc5.h-to-fix-builds-with-gcc5.patch \
	   file://disable-gcc-check.patch \
	   file://igep0050.patch \
	   file://tca641x.patch \
	   file://smsc75xx.patch \
	  "

S = "${WORKDIR}/git"

PACKAGE_ARCH = "${MACHINE_ARCH}"
PARALLEL_MAKE = ""

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"

UBOOT_MACHINE = "omap5_igep0050_defconfig"

UBOOT_BINARY ?= "u-boot.bin"
UBOOT_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.bin"
UBOOT_SYMLINK ?= "u-boot.bin"
UBOOT_MAKE_TARGET ?= "all"

MLO_IMAGE ?= "MLO-${MACHINE}-${PV}-${PR}"
MLO_SYMLINK ?= "MLO"

do_configure_prepend () {
	mkdir -p ${S}/board/isee/igep0050
	cp ${WORKDIR}/Kconfig ${S}/board/isee/igep0050/
	cp ${WORKDIR}/Makefile ${S}/board/isee/igep0050/
	cp ${WORKDIR}/board.c ${S}/board/isee/igep0050/
	cp ${WORKDIR}/board_configuration.c ${S}/board/isee/igep0050/
	cp ${WORKDIR}/board_configuration.h ${S}/board/isee/igep0050/
	cp ${WORKDIR}/mux_data.h ${S}/board/isee/igep0050/
	cp ${WORKDIR}/omap5_igep0050_defconfig ${S}/configs/
	cp ${WORKDIR}/omap5_igep0050.h ${S}/include/configs/
	cp ${WORKDIR}/smsc75xx.c ${S}/drivers/usb/eth/
}

do_configure () {
	oe_runmake ARCH=arm ${UBOOT_MACHINE}

	sed -i -e s,NFS_IP,${MA_NFS_IP},g ${WORKDIR}/boot-nfs.script
	sed -i -e s,NFS_PATH,${MA_NFS_PATH},g ${WORKDIR}/boot-nfs.script

	sed -i -e s,NFS_IP,${MA_NFS_IP},g ${WORKDIR}/boot-nfs2.script
	sed -i -e s,NFS_PATH,${MA_NFS_PATH},g ${WORKDIR}/boot-nfs2.script
	sed -i -e s,TARGET_IP,${MA_TARGET_IP},g ${WORKDIR}/boot-nfs2.script
	sed -i -e s,GATEWAY_IP,${MA_GATEWAY_IP},g ${WORKDIR}/boot-nfs2.script
}

do_compile () {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	oe_runmake ARCH=arm ${UBOOT_MAKE_TARGET}
	oe_runmake tools env HOSTCC="cc"
}

do_install () {
	install -d ${D}/boot
	install -m 0644 ${S}/${UBOOT_BINARY} ${D}/boot/${UBOOT_IMAGE}
	ln -sf ${UBOOT_IMAGE} ${D}/boot/${UBOOT_BINARY}

	install -m 0644 ${S}/MLO ${D}/boot/${MLO_IMAGE}
	ln -sf ${MLO_IMAGE} ${D}/boot/${MLO_SYMLINK}

	install -m 0644 ${WORKDIR}/boot-sdcard.script ${D}/boot/uEnv-sdcard.txt
	install -m 0644 ${WORKDIR}/boot-nfs.script ${D}/boot/uEnv-nfs.txt
	install -m 0644 ${WORKDIR}/boot-nfs2.script ${D}/boot/uEnv-nfs2.txt
	ln -sf uEnv-label.txt ${D}/boot/uEnv.txt
}

FILES_${PN} = "/boot"
# no gnu_hash in uboot.bin, by design, so skip QA
INSANE_SKIP_${PN} = True

PACKAGES += "${PN}-fw-utils"
FILES_${PN}-fw-utils = "${sysconfdir} ${base_sbindir}"
# u-boot doesn't use LDFLAGS for fw files, needs to get fixed, but until then:
INSANE_SKIP_${PN}-fw-utils = True

do_deploy () {
	install -d ${DEPLOY_DIR_IMAGE}
	install -m 0644 ${S}/${UBOOT_BINARY} ${DEPLOY_DIR_IMAGE}/${UBOOT_IMAGE}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_IMAGE}

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_SYMLINK}

	install -m 0644 ${S}/MLO ${DEPLOY_DIR_IMAGE}/${MLO_IMAGE}
	ln -sf ${MLO_IMAGE} ${DEPLOY_DIR_IMAGE}/${MLO_SYMLINK}

	install -m 0644 ${D}/boot/uEnv-nfs.txt ${DEPLOY_DIR_IMAGE}
	rm -f uEnv.txt
	ln -sf uEnv-label.txt uEnv.txt
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/uEnv.txt
}
do_deploy[dirs] = "${S}"
addtask deploy before do_package_stage after do_compile

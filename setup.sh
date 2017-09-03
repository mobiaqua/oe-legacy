#!/bin/sh

error() {
	echo
	echo "* ERROR * " $1
	echo
	[ "x$0" = "x./setup.sh" ] && exit 1
	ERROR=1
}

python_v3_check() {
	ver=`/usr/bin/env python --version 2>&1 | grep "Python 3"`
	if [ "${ver}" != "" ]; then
		return 1
	else
		return 0
	fi
}

get_os() {
	if [ -e /bin/uname ]; then
		OS=`/bin/uname -s`
	elif [ -e /usr/bin/uname ]; then
		OS=`/usr/bin/uname -s`
	else
		OS=`uname -s`
	fi
	export $OS
}

gnutools="[ awk b2sum base32 base64 basename cat chcon chgrp chmod chown chroot cksum comm cp \
csplit cut date dd df dir dircolors dirname du echo env expand expr factor false fmt fold \
groups head hostid id install join kill link ln logname ls md5sum mkdir mkfifo mknod mktemp \
mv nice nl nohup nproc numfmt od paste pathchk pinky pr printenv printf ptx pwd readlink \
realpath rm rmdir runcon sed seq sha1sum sha224sum sha256sum sha384sum sha512sum shred shuf \
sleep sort split stat stdbuf stty sum sync tac tail tee test timeout touch tr true \
truncate tsort tty uname unexpand uniq unlink uptime users vdir wc who whoami yes"

prepare_tools() {
	OE_BASE=`pwd -P`
	/bin/rm -f ${OE_BASE}/oe/bin/deftar
	/bin/rm -f ${OE_BASE}/oe/bin/tar
	/bin/rm -f ${OE_BASE}/oe/bin/\]
	/bin/rm -f ${OE_BASE}/oe/bin/\[

	get_os
	case $OS in
	Darwin)
		if [ -e /opt/local/bin/gnutar ]; then
			/bin/ln -s /opt/local/bin/gnutar ${OE_BASE}/oe/bin/tar
		elif [ -e /sw/bin/gtar ]; then
			/bin/ln -s /sw/bin/gtar ${OE_BASE}/oe/bin/tar
		fi
		if [ -e /usr/bin/tar ]; then
			/bin/ln -s /usr/bin/tar ${OE_BASE}/oe/bin/deftar
		fi
		for i in $gnutools; do
			/bin/rm -f ${OE_BASE}/oe/bin/$i
			if [ -e /opt/local/bin/g$i ]; then
				/bin/ln -s /opt/local/bin/g$i ${OE_BASE}/oe/bin/$i
			elif [ -e /sw/sbin/g$i ]; then
				/bin/ln -s /sw/sbin/g$i ${OE_BASE}/oe/bin/$i
			fi
		done

		if [ ! -e /opt/local/bin/gnutar ] && [ ! -e /sw/bin/gtar ]; then
			echo "* ERROR *  Missing GNU tar!"
			return 1
		fi
		if [ ! -e /opt/local/bin/gsed ] && [ ! -e /sw/bin/gsed ]; then
			echo "* ERROR *  Missing GNU sed!"
			return 1
		fi
		if [ ! -e /opt/local/bin/ginstall ] && [ ! -e /sw/bin/ginstall ]; then
			echo "* ERROR *  Missing GNU coreutils!"
			return 1
		fi
		;;
	Linux)
		if [ -e /bin/tar ]; then
			/bin/ln -s /bin/tar ${OE_BASE}/oe/bin/deftar
		fi
	esac

	return 0
}

setup() {
	export OE_BASE=`${OE_BASE}/oe/bin/readlink -f "$OE_BASE"`

	if [ "$1" = "tv" ]; then
		export DISTRO=mobiaqua-tv
		export MACHINE=board-tv
		image=rootfs-devel-tv
		ARMDIR=armv7a-hf
	elif [ "$1" = "car" ]; then
		export DISTRO=mobiaqua-car
		export MACHINE=igep0030
		image=rootfs-devel-car
		ARMDIR=armv7a-hf
	fi

	if [ -e ${HOME}/.mobiaqua/oe/${DISTRO}_defaults ]; then
		. ${HOME}/.mobiaqua/oe/${DISTRO}_defaults
		echo "Reading custom settings from file '${HOME}/.mobiaqua/oe/${DISTRO}_defaults'"
	else
		echo "No custom settings file: '${HOME}/.mobiaqua/oe/${DISTRO}_defaults'"
		echo "Using defaults instead."
	fi

	MA_DL_DIR=${MA_DL_DIR:="$HOME/sources"}
	export MA_TARGET_IP=${MA_TARGET_IP:="192.168.1.10"}
	export MA_TARGET_MAC=${MA_TARGET_MAC:=""}
	export MA_DNS_IP=${MA_DNS_IP:="192.168.1.1"}
	export MA_NFS_IP=${MA_NFS_IP:="192.168.1.1"}
	export MA_NFS_PATH=${MA_NFS_PATH:="/nfsroot"}
	export MA_ROOT_PASSWORD=${MA_ROOT_PASSWORD:=""}
	export MA_DROPBEAR_KEY_FILE="$HOME/.mobiaqua/oe/${DISTRO}_dropbear_rsa_host_key"
	export MA_FSTAB_FILE="$HOME/.mobiaqua/oe/${DISTRO}_fstab"
	export MA_ROOTFS_POSTPROCESS=${MA_ROOTFS_POSTPROCESS:="echo"}
	export MA_JTAG_ADAPTER=${MA_JTAG_ADAPTER:=""}
	export BB_ENV_EXTRAWHITE="MA_TARGET_IP MA_TARGET_MAC MA_DNS_IP MA_NFS_IP MA_NFS_PATH \
			MA_ROOT_PASSWORD MA_DROPBEAR_KEY_FILE MA_FSTAB_FILE MA_ROOTFS_POSTPROCESS \
			MA_JTAG_ADAPTER"

	echo "--- Settings:"
	echo " -  sources:    ${MA_DL_DIR}"
	echo " -  target ip:  ${MA_TARGET_IP}"
	echo " -  target mac: ${MA_TARGET_MAC}"
	echo " -  dns ip:     ${MA_DNS_IP}"
	echo " -  nfs ip:     ${MA_NFS_IP}"
	echo " -  nfs path:   ${MA_NFS_PATH}"
	if [ "$MA_ROOT_PASSWORD" != "" ]; then
		echo " -  root password is defined"
	else
		echo " -  root password is NOT defined"
	fi
	if [ -f ${MA_DROPBEAR_KEY_FILE} ]; then
		echo " -  target dropbear host key file found"
	else
		echo " -  target dropbear host key file NOT found"
	fi
	if [ -f ${MA_FSTAB_FILE} ]; then
		echo " -  target fstab file found"
	else
		echo " -  target fstab file NOT found"
	fi
	if [ "${MA_ROOTFS_POSTPROCESS}" != "" ]; then
		echo " -  rootfs postprocess commands are defined"
	else
		echo " -  rootfs postprocess commands are NOT defined"
	fi
	if [ "${MA_JTAG_ADAPTER}" != "" ]; then
		echo " -  JTAG adapter: ${MA_JTAG_ADAPTER}"
	else
		echo " -  JTAG adapter is NOT defined"
	fi
	mkdir -p ${OE_BASE}/build-${DISTRO}/conf

	BBF="\${OE_BASE}/oe/recipes/*/*.bb"

	DL_DIR=${DL_DIR:="$HOME/sources"}

	if [ ! -f ${OE_BASE}/build-${DISTRO}/conf/local.conf ] || [ ! -f ${OE_BASE}/build-${DISTRO}/env.source ] || [ "$1" = "--force" ]; then
		PATH_TO_TOOLS="build-${DISTRO}/tmp/sysroots/`uname -m`-`uname -s | awk '{print tolower($0)}'`/usr"
		echo "DL_DIR = \"${DL_DIR}\"
OE_BASE = \"${OE_BASE}\"
BBFILES = \"${BBF}\"
MACHINE = \"${MACHINE}\"
TARGET_OS = \"linux-gnueabi\"
DISTRO = \"${DISTRO}\"
INHERIT = \"rm_work\"
IMAGE_KEEPROOTFS = \"1\"
CACHE = \"${OE_BASE}/build-${DISTRO}/cache/oe-cache.\${USER}\"
ASSUME_PROVIDED += \" git-native perl-native python-native desktop-file-utils-native linux-libc-headers-native glib-2.0-native intltool-native xz-native\"
PARALLEL_MAKE = \"-j 4\"
BB_NUMBER_THREADS = \"3\"
" > ${OE_BASE}/build-${DISTRO}/conf/local.conf



		echo "OE_BASE=\"${OE_BASE}\"
export BBPATH=\"\${OE_BASE}/oe/:\${OE_BASE}/bb/:\${OE_BASE}/build-${DISTRO}/\"
if [ ! \`echo \${PATH} | grep \${OE_BASE}/bb/bin\` ]; then
	export PATH=\${OE_BASE}/bb/bin:\${OE_BASE}/oe/bin:\${PATH}
fi
unset LD_LIBRARY_PATH
export LD_LIBRARY_PATH=
export PYTHONPATH=${OE_BASE}/bb/lib
export LANG=C
unset TERMINFO
unset GCONF_SCHEMA_INSTALL_SOURCE
export MA_JTAG_ADAPTER=${MA_JTAG_ADAPTER}
" > ${OE_BASE}/build-${DISTRO}/env.source



		echo "source ${OE_BASE}/build-${DISTRO}/env.source
if [ ! \`echo \${PATH} | grep ${ARMDIR}/bin\` ]; then
	export PATH=${OE_BASE}/${PATH_TO_TOOLS}/${ARMDIR}/bin:${OE_BASE}/${PATH_TO_TOOLS}/bin:\${PATH}
fi
export CROSS_COMPILE=arm-linux-gnueabi-
" > ${OE_BASE}/build-${DISTRO}/crosstools-setup



		echo "--- Created:"
		echo " -  ${OE_BASE}/build-${DISTRO}/conf/local.conf,"
		echo " -  ${OE_BASE}/build-${DISTRO}/env.source,"
		echo " -  ${OE_BASE}/build-${DISTRO}/crosstools-setup ---"
	fi

	echo
	echo "--- MobiAqua OE configuration finished ---"
	echo
	echo "--- Usage example: bitbake $image ---"
	echo
}

bitbake() {
	cd ${OE_BASE}/build-${DISTRO} && source env.source && ${OE_BASE}/bb/bin/bitbake $@
}

ERROR=
[ "x$0" = "x./setup.sh" ] && error "Script must run via sourcing like '. setup.sh'"

[ "$ERROR" != "1" ] && [ $EUID -eq 0 ] && error "Script running with superuser privileges! Aborting."

[ "$ERROR" != "1" ] && [ -z "$BASH_VERSION" ] && error "Script NOT running in 'bash' shell"

[ "$ERROR" != "1" ] && [ "x$1" != "xtv" ] && [ "x$1" != "xcar" ] && error "Not supported target!"

[ "$ERROR" != "1" ] && python_v3_check; [ "$?" != "0" ] && error "Python v3 is not compatible please install v2"

[ "$ERROR" != "1" ] && prepare_tools; [ "$?" != "0" ] && error "Please install missing tools"

[ "$ERROR" != "1" ] && setup $1
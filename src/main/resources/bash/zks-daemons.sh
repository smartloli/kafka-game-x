#! /bin/bash

# 设置Zookeeper集群节点地址
hosts=(dn1 dn2 dn3)

# 获取输入命令参数
cmd=$1

# 执行分布式管理命令	
function zookeeper()
{
	for i in ${hosts[@]}
		do
			#echo Zookeeper node is $i, run $cmd command.
			ssh hadoop@$i "source /etc/profile;zkServer.sh $cmd;echo Zookeeper node is $i, run $cmd command." &
			sleep 1
		done
}	

# 判断输入的Zookeeper命令参数是否有效
case "$1" in
    start)
        zookeeper
        ;;
    stop)
        zookeeper
        ;;
    status)
         zookeeper
        ;;
    start-foreground)
         zookeeper
        ;;
    upgrade)
         zookeeper
        ;;
    restart)
        zookeeper
        ;;
    print-cmd)
        zookeeper
        ;;
    *)
        echo "Usage: $0 {start|start-foreground|stop|restart|status|upgrade|print-cmd}"
        RETVAL=1
esac

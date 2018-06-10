#! /bin/bash

# Kafka代理节点地址
hosts=(dn1 dn2 dn3)

# 打印启动分布式脚本信息
mill=`date "+%N"`
tdate=`date "+%Y-%m-%d %H:%M:%S,${mill:0:3}"`

echo [$tdate] INFO [Kafka Cluster] begins to execute the $1 operation.

# 执行分布式开启命令	
function start()
{
	for i in ${hosts[@]}
		do
			smill=`date "+%N"`
			stdate=`date "+%Y-%m-%d %H:%M:%S,${smill:0:3}"`
			ssh hadoop@$i "source /etc/profile;echo [$stdate] INFO [Kafka Broker $i] begins to execute the startup operation.;kafka-server-start.sh $KAFKA_HOME/config/server.properties>/dev/null" &
			sleep 1
		done
}	

# 执行分布式关闭命令	
function stop()
{
	for i in ${hosts[@]}
		do
			smill=`date "+%N"`
			stdate=`date "+%Y-%m-%d %H:%M:%S,${smill:0:3}"`
			ssh hadoop@$i "source /etc/profile;echo [$stdate] INFO [Kafka Broker $i] begins to execute the shutdown operation.;kafka-server-stop.sh>/dev/null;" &
			sleep 1
		done
}

# 查看Kafka代理节点状态
function status()
{
	for i in ${hosts[@]}
		do
			smill=`date "+%N"`
			stdate=`date "+%Y-%m-%d %H:%M:%S,${smill:0:3}"`
			ssh hadoop@$i "source /etc/profile;echo [$stdate] INFO [Kafka Broker $i] status message is :;jps | grep Kafka;" &
			sleep 1
		done
}

# 判断输入的Kafka命令参数是否有效
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {start|stop|status}"
        RETVAL=1
esac


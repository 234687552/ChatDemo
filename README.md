һ��android ��module���� easeui ���޸���gradle��������
=====
### ���裺
	1.File����new����impor Module
	2.source directory��ѡ����sdk/example/easeui�ļ� һ·next��finish��
	3.�������File����Project Structure ���������modulesѡ��app��������
		1)ѡ���ұ�DependenciesȻ��ѡ+��
		2)ѡ��Moudle dependency 
		3)��easeuiѡ������Ϊ�Լ���lib��
	3.����build.gradle(Module:easeui) 
		1)compilesdkversion ��buildtoolsversion ��defaultcofig �Ĳ���������build.gradle(Module:app)һ��
		2)dependencies ������� compile 'com.android.support:appcompat-v7:23.4.0'(��appde gradle��������)
		3)ɾ��easeui�ļ�������support-v4.jar�������Ϊ��һ���Ѿ��ﵼ��(v7����v4)���ص��Ǳ����ظ����method��
	4.����build.gradle(Module:app)
		1)ע�͵� //compile 'com.android.support:appcompat-v7:23.4.0' ��Ϊlib��easeui��æ�����ˡ������ظ�����
		2)ȷ������compile project(':easeui')��֤�ڶ�������ɹ���
		3)ȥ�� compile files('libs/hyphenatechat_3.1.4.jar') ��Ϊeaseui����lib�ļ������Ѿ�����/hyphenatechat_3.1.4.jar��
	5.ȥ�Լ���Ŀ�ļ�����(���ҵ�ChatDemo�ļ�����) ��lib�ļ���ɾ��hyphenatechat_3.1.4.jar����Ϊeaseui�ļ������С�
		
### �ο���ַ��
	http://stackoverflow.com/questions/32807587/com-android-build-transform-api-transformexception/32811133#32811133
	
	http://blog.sina.com.cn/s/blog_6f3828770102w30b.html
	
	http://stackoverflow.com/questions/32798816/unexpected-top-level-exception-com-android-dex-dexexception-multiple-dex-files/37202393#37202393
	
### �ĵ��ܽ᣺
	
	�������� �����ظ��������� ���º��й�ϵ�� ��support-v7����v4��
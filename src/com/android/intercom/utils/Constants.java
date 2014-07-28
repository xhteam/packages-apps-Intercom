package com.android.intercom.utils;

public class Constants {	
	public static final int  RIL_REQUEST_PTT_QUERY_AVAILABLE_GROUPS = 0x11;
	public static final int  RIL_REQUEST_PTT_GROUP_SETUP  = 0x12;
	public static final int  RIL_REQUEST_PTT_GROUP_RELEASE = 0x13;
	public static final int  RIL_REQUEST_PTT_CALL_DIAL = 0x14;
	public static final int  RIL_REQUEST_PTT_CALL_HANGUP = 0x15;
	public static final int  RIL_REQUEST_PTT_CURRENT_GROUP_SCANLIST_UPDATE = 0x16;
	//TODO::
	//RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR do not know the input args
	public static final int  RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR = 0x17;
	public static final int  RIL_REQUEST_PTT_DEVICE_INFO = 0x18;
	public static final int  RIL_REQUEST_PTT_QUERY_BIZ_STATE = 0x19;
	
	public static final int RIL_UNSOL_PTT_CALL_INDICATOR = 0x21;/*
																 * When call
																 * dial , return
																 * call if
																 * successfully.
																 */
	public static final int RIL_UNSOL_PTT_GROUP_RELEASE = 0x22;
	public static final int RIL_UNSOL_PTT_NOTIFICATION_CALL = 0x23;//状态更新
	public static final int RIL_UNSOL_PTT_CALL_CONNECT = 0x24;
	public static final int RIL_UNSOL_PTT_CALL_HANGUP = 0x25;
	public static final int RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS = 0x26;
	public static final int RIL_UNSOL_PTT_BLOCKED_INDICATOR = 0x27;
	public static final int RIL_UNSOL_PTT_AVAILABLE_GROUP_CHANGED = 0x28;
	public static final int RIL_UNSOL_PTT_NOTIFICATION_DIAL = 0x29;
	public static final int RIL_UNSOL_PTT_CURRENT_GROUP_ACTIVE_LIST = 0x30;
	public static final int RIL_UNSOL_PTT_DEVICE_INFO = 0x31;
	public static final int RIL_UNSOL_PTT_GROUP_OWNER = 0x32;
	public static final int RIL_UNSOL_PTT_TRUNKING_MODE = 0x33;
	public static final int RIL_UNSOL_PTT_NOTIFICATION_JOIN_GROUP = 0x34;
	public static final int RIL_UNSOL_PTT_BIZ_INFO = 0x35;
	public static final int RIL_UNSOL_PTT_NETWORK_VERSION = 0x36;
	
	
	public static final int GRANT_SUCCESS = 0;
	public static final int GRANT_FAIL = 1;
	
	public static final String EXTRA_UE_STATUS = "ue_status";	
	public static final String STATUS_CHANGED_ACTION = "com.android.intercom.uestatusChanged";
	public static final String GROUP_LIST_CHANGED_ACTION = "com.android.intercom.groupListChanged";
	
	public Constants() {
		// TODO Auto-generated constructor stub
	}

	public enum OBJECT_TYPE{
		GROUP_OBJECT,UE_OBJECT
	}
	
	public enum CALL_PRIORITY_TYPE{
		PRIORITY_0(0),PRIORITY_1(1),PRIORITY_2(2),PRIORITY_3(3),PRIORITY_4(4),PRIORITY_5(5),PRIORITY_6(6),
		PRIORITY_7(7),PRIORITY_8(8),PRIORITY_9(9),PRIORITY_10(10),PRIORITY_11(11),PRIORITY_12(12),PRIORITY_13(13),
		PRIORITY_14(14),PRIORITY_15(15);
		private int value;
		public int getValue(){
			return this.value;
		}
		private CALL_PRIORITY_TYPE(int value){
			this.value = value;
		}
	}
	
	public enum AI_SERVICE_TYPE{
		AUDIO_GROUP_CALL(0), AUDIO_GROUP_BROADCAST(1),VIDEO_GROUP_CALL(2),VIDEO_BROADCAST(3),AUDIO_POINT_CALL(4),VIDEO_POINT_CALL(5);
		private int value;
		public int getValue(){
			return this.value;
		}
		private AI_SERVICE_TYPE(int value){
			this.value = value;
		}
	}

	public enum GROUP_OWNER_IND_TYPE {
		IS_GROUP_OWNER(0), NOT_GROUP_OWNER(1);
		private int value;
		public int getValue(){
			return this.value;
		}

		private GROUP_OWNER_IND_TYPE(int value){
			this.value = value;
		}
	}
	
	/*public  enum RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE{
		NO_NETWORK(1001), NOT_SIGN_GROUP(1108), UE_NOT_SUPORT(1004), UE_REMOTE_CLOSED_FOR_TEMP(
				1011), UE_REMOTE_CLOSED_FOREVER(1012);
		private int value;
		public int getValue(){
			return this.value;
		}
		private RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE(int value){
			this.value = value;
		}
	}*/
	public  class RIL_REQUEST_PTT_GROUP_SETUP_RESULT_TYPE{
		public final static int NO_NETWORK=1001;
		public final static int NOT_SIGN_GROUP = 1108;
		public final static int UE_NOT_SUPORT = 1004;
		public final static int UE_REMOTE_CLOSED_FOR_TEMP = 1011;
		public final static int UE_REMOTE_CLOSED_FOREVER = 1012;
	}
	
	public class RIL_REQUEST_PTT_GROUP_RELEASE_RESULT_TYPE{
		public final static int UE_HAS_NO_RIGHT_CLOSED_GROUP = 1003;
		public final static int UE_CLOSE_GROUP_SUCCESS = 0;
		
	}
	
	public class RIL_UNSOL_PTT_CALL_INDICATOR_RESULT_TYPE{
		public final static int NO_NETWOR = 1001;
		public final static int NOT_SIGN_GROUP = 1108;
		public final static int UE_REMOTE_CLOSED_FOR_TEMP = 1011;
		public final static int UE_REMOTE_CLOSED_FOREVER = 1012;
		
	}
	
	public class RIL_UNSOL_PTT_NOTIFICATION_DIAL_STATUS_TYPE{
		public final static String CALL_RIGHT_IDEL = "0";
		public final static String CALL_RIGHT_IN_LINE = "1"; 
	}
	
	public class RIL_REQUEST_PTT_QUERY_BLOCKED_INDICATOR_TYPE{
		public final static int IMSI_OPEN_IMEI_OPERN = 0;
		public final static int IMSI_TEMP_CLOSE_TEMP_IMEI_OPEN = 1;
		public final static int IMSI_CLOSE_FOREVER_IMEI_OPEN = 2;
		public final static int IMSI_OPEN_IMEI_CLOSE_TEMP = 3;
		public final static int IMSI_OPEN_IMEI_CLOSE_FOREVER = 4;
		public final static int IMSI_CLOSE_TEMP_IMEI_CLOSE_FOREVER = 5;
		public final static int IMSI_CLOSE_FOREVER_IMEI_CLOSE_TEMP = 6;
		public final static int IMSI_CLOSE_TEMP_IMEI_CLOSE_TEMP = 7;
		public final static int IMSI_CLOSE_FOREVER_IMEI_CLOSE_FOREVER = 8;
		public final static int IMSI_IMEI_EXCEPTION  = 0xffff;
		
	}
	
	public class RIL_UNSOL_PTT_CALL_CONNECT_STATUS_TYPE{
		public final static int CONNECT_FAIL = 0;
		public final static int CONNECT_SUCCESS = 1;
	}
	
	public class RIL_UNSOL_PTT_OUTGOING_CALL_PROGRESS_STATUS_TYPE{		
		public final static int CALLED_PROGRESSING = 0;
		public final static int CALLED_QUEUED = 1;
		public final static int CALLED_PARTY_PAGED = 2;
		public final static int CALL_CONTINUE = 3;
		public final static int HANG_TIME_EXPIRED = 4;
	}
	
	
}

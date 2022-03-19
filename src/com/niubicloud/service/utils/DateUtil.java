package com.niubicloud.service.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtil {
    
    /**
     * ���ڸ�ʽ �� ��2009
     */
    public static final String DATEFORMATYEAR = "yyyy";
    
    /**
     * ���ڸ�ʽ �� ��  �� 2009-02
     */
    public static final String DATEFORMATMONTH = "yyyy-MM";
    
    /**
     * ���ڸ�ʽ �� �� �� ��2009-02-26
     */
    public static final String DATEFORMATDAY = "yyyy-MM-dd";
    
    /**
     * ���ڸ�ʽ �� �� �� ʱ ��2009-02-26 15
     */
    public static final String DATEFORMATHOUR = "yyyy-MM-dd HH";
    
    /**
     * ���ڸ�ʽ �� �� �� ʱ �� ��2009-02-26 15:40
     */
    public static final String DATEFORMATMINUTE = "yyyy-MM-dd HH:mm";

    /**
     * ���ڸ�ʽ�� �� �� ʱ �� �� �� 2009-02-26 15:40:00
     */
    public static final String DATEFORMATSECOND = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * ���ڸ�ʽ�� �� �� ʱ �� �� ���� ��2009-02-26 15:40:00 110
     */
    public static final String DATEFORMATMILLISECOND = "yyyy-MM-dd HH:mm:ss SSS";
    
    public static String getGMTOffsetTime(int offset) {
    	Calendar cd = Calendar.getInstance();
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    	sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    	
    	cd.add(Calendar.SECOND,offset);
    	
    	return sdf.format(cd.getTime());
    }
    
    /**
     * �������� �������ݿ����� str_to_date to_date ��ȷ����
     * 
     * @return
     */
    public static String str_to_date_second(String dbtype,String dataStr) {
        String ret = "";
        if (dbtype.equalsIgnoreCase("oracle")) { //Oracle���ݿ�
            ret += " to_date('" + dataStr + "','yyyy-MM-dd hh24:mi:ss') ";
        } else { //MySQL���ݿ�
            ret += " str_to_date('" + dataStr + "','%Y-%m-%d %H:%i:%s') ";
        }
        return ret;
    }
    
    /**
     * �������� �������ݿ����� str_to_date to_date ��ȷ����
     * 
     * @return
     */
    public static String str_to_date_minute(String dbtype,String dataStr) {
        String ret = "";
        if (dbtype.equalsIgnoreCase("oracle")) { //Oracle���ݿ�
            ret += " to_date('" + dataStr + "','yyyy-MM-dd hh24:mi') ";
        } else { //MySQL���ݿ�
            ret += " str_to_date('" + dataStr + "','%Y-%m-%d %H:%i') ";
        }
        return ret;
    }
    
    /**
     * ��ָ���ĸ�ʽ����Dateת����String ��dateΪnull,����null
     * 
     * @param date
     *            Date����
     * @param format
     *            ���ڸ�ʽ
     * @return String
     */
    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }
    
    /**
     * ��ָ���ĸ�ʽ����stringת����Date ��stringΪ�ջ�null������null
     * 
     * @param string
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parase(String string, String format) throws ParseException {
        if (StringUtil.isEmpty(string)) {
            return null;
        }
        return new SimpleDateFormat(format).parse(string);
    }
    
    /**
     * �������ո�ʽ����Stringת����Date ���StringΪ�ջ���null������null
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date string2Date(String dateString) throws ParseException {
        Date dateTime = null;
        if (!StringUtil.isEmpty(dateString)) { //���stringʱ��������ǿ�
            final SimpleDateFormat df = new SimpleDateFormat(DATEFORMATDAY); //������ʱ���ʽ��
            Date date = null;
            date = df.parse(dateString); //Stringת��Date
            dateTime = new Date(date.getTime());
        }
        return dateTime;
    }
    
    /**
     * ��ȡ��ǰϵͳʱ��
     * 
     * @return
     */
    public static Date getSysDate() {
        Calendar calender = Calendar.getInstance();
        return calender.getTime();
    }
    
    /**
     * ȡһ��Ŀ�ʼʱ�� ��ȷ���� ���dateΪnull������null
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDayFirstSecond(Date date) {
        if (date == null) {
            return null;
        }
        String str = format(date, DATEFORMATDAY) + " 00:00:00";
        return str;
    }
    
    /**
     * ȡһ��Ŀ�ʼʱ�� ��ȷ���� ���stringΪ""������null
     *
     * @return
     * @throws Exception
     */
    public static String getDayFirstSecond(String date) {
        if (date.equals("")) {
            return null;
        }
        String ret = "";
        try {
            ret = getDayFirstSecond(string2Date(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * ȡһ��Ľ���ʱ�� ��ȷ���� ���dateΪnull������null
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static String getDayLastSecond(Date date) {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 23:59:59";
        return str;
    }
    
    /**
     * ȡһ��Ľ���ʱ�� ��ȷ���� ���stringΪ""������null
     *
     * @return
     * @throws Exception
     */
    public static String getDayLastSecond(String date) {
        if (date.equals("")) {
            return null;
        }
        String ret = "";
        try {
            ret = getDayLastSecond(string2Date(date));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * ȡһ��Ŀ�ʼʱ�� ��ȷ������
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDayFirstTime(Date date) throws Exception {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 00:00:00 000";
        return parase(str, DATEFORMATMILLISECOND);
    }
    
    /**
     * ȡһ��Ľ���ʱ�� ��ȷ������
     * 
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDayLastTime(java.util.Date date) throws Exception {
        if (date == null) {
            return null;
        }
        final String str = format(date, DATEFORMATDAY) + " 23:59:59 999";
        return parase(str, DATEFORMATMILLISECOND);
    }
    
    /**
     * ��ȡ���������
     * 
     * @param strDate
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getYestoday(String strDate) throws ParseException {
        if (null != strDate && strDate.length() > 0) {
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(parase(strDate, DATEFORMATDAY));
            cal.add(Calendar.DAY_OF_MONTH, -1);
            final String str = format(cal.getTime(), DATEFORMATDAY);
            return parase(str, DATEFORMATDAY);
        }
        return null;
    }
    
    /**
     * ��ȡ���������
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getTomorrow() throws ParseException {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(getSysDate());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        final String str = format(cal.getTime(), DATEFORMATDAY);
        return parase(str, DATEFORMATDAY);
    }
    
    /**
     * ��ȡָ��������һ�������
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static Date getNextDay(Date someDate) throws ParseException {
        final Calendar ca = Calendar.getInstance();
        ca.setTime(someDate);
        ca.add(Calendar.DAY_OF_MONTH, 1);
        final String str = format(ca.getTime(), DATEFORMATDAY);
        return parase(str, DATEFORMATDAY);
    }
    
    /**
     * ���ݵ�ǰ���ڷ��ر��µ����һ��
     * 
     * @param someDate
     * @return
     */
    public static Date getLastDayOfMonth(Date someDate) {
        final Calendar ca = Calendar.getInstance();
        ca.setTime(someDate); // someDate Ϊ��Ҫ��ȡ���Ǹ��µ�ʱ��
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }
    
    /**
     * �õ��������һ�������
     */
    public static Date getLastDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        return ca.getTime();
    }
    
    /**
     * ��ǰ���� yyyy-MM-dd
     * 
     * @return
     */
    public static String getToday() {
        Calendar ca = Calendar.getInstance();
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }
    
    /**
     * ��ǰ�����ϸ��� yyyy-MM-dd
     * 
     * @return
     */
    public static String getLastMonthToday() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - 1);
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }
    
    /**
     * ��ǰ�����ϸ�����yyyy-MM-dd
     * 
     * @return
     */
    public static String getLastWeekToday() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -7);
        String str = format(ca.getTime(), DATEFORMATDAY);
        return str;
    }
    
    /**
     * ��ǰ���� yyyy-MM-dd HH:mm:ss
     * 
     * @return
     */
    public static String getTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }
    
    /**
     * ��ǰ����-�� yyyy-MM-dd HH:mm:ss
     * 
     * @return
     */
    public static String getLastMonthTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MONTH, ca.get(Calendar.MONTH) - 1);
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }
    
    /**
     * ��ǰ����-һ�� yyyy-MM-dd HH:mm:ss
     * 
     * @return
     */
    public static String getLastWeekTodayToSecond() {
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.DAY_OF_MONTH, -7);
        String str = format(ca.getTime(), DATEFORMATSECOND);
        return str;
    }
    
    /**
     * �õ����µ�һ�������
     */
    public static Date getStartDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }
    
    /**
     * �õ����µ�һ�������
     */
    public static Date getStartDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }
    
    /**
     * �õ��������һ�������
     */
    public static Date getEndDayOfMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }
    
    /**
     * �õ��������һ�������
     */
    public static Date getEndDayOfMonth(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = sdf.parse(dateStr);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cDay.getTime();
    }
    
    /**
     * �õ��¸��µ�һ�������
     */
    public static Date getStartDayOfNextMonth(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.add(Calendar.MONTH, +1);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }
    
    /**
     * �õ��¸��µ�һ�������
     */
    public static Date getStartDayOfNextMonth(String dateStr) throws ParseException {
        Date date = parase(dateStr, DATEFORMATMONTH);
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.add(Calendar.MONTH, +1);
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        return cDay.getTime();
    }
    
    /**
     * ��ȡָ�����������ܵ���һ
     */
    public static Date getMonday(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        cDay.set(Calendar.DAY_OF_WEEK, 2);// ���⽫���ն�λ��һ�죬��һȡ�ڶ���
        return cDay.getTime();
    }

    /**
     * ��ȡָ��������������
     */
    public static Date getSunday(Date date) {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(date);
        if (Calendar.DAY_OF_WEEK == cDay.getFirstDayOfWeek()) { // ����պ������գ�ֱ�ӷ���
            return date;
        } else {// ����������գ���һ�ܼ���
            cDay.add(Calendar.DAY_OF_YEAR, 7);
            cDay.set(Calendar.DAY_OF_WEEK, 1);
            return cDay.getTime();
        }
    }
    
    /**
     * ��ȡ����ĵ�һ��
     */
    public static Date getFirstDayOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    
    /**
     * ��ȡ����ĵ�һ��
     */
    public static Date getFirstDayOfYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    
    /**
     * ��ȡ��������һ��
     */
    public static Date getLastDayOfYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        return calendar.getTime();
    }
    
    /**
     * ��ȡ��������һ��
     */
    public static Date getLastDayOfYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        return calendar.getTime();
    }
    
    /**
     * ��ȡ��һ��ĵ�һ��
     */
    public static Date getFirstDayOfNextYear(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    
    /**
     * ��ȡ��һ��ĵ�һ��
     */
    public static Date getFirstDayOfNextYear(String dateStr) throws ParseException{
        Date date = parase(dateStr, DATEFORMATYEAR);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }
    
    /**
     * ��ȡ���������
     *
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public static String getYestoday() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE, -1);
        calendar.set(calendar.HOUR_OF_DAY, 0);
        calendar.set(calendar.MINUTE, 0);        
        return format(calendar.getTime(), DATEFORMATMINUTE);
    }
    
    /**
     * ��ȡ��ǰʱ�����֮ǰ ��ȷ������
     */
    public static String getBeforMinutesSysDate(int minute) throws ParseException{
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -minute);
        return  format(calendar.getTime(), DATEFORMATMINUTE);
    }
    
    /**
     *  ��ȡ��ǰʱ�����֮ǰ ��ȷ������
     */
    public static String getMinuteSysDate() throws ParseException{
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), DATEFORMATMINUTE);
    }
    
}
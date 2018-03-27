package alpha_vantage.mappers;

import alpha_vantage.model.internal.Past30Days;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public class DigitalDailyMapper {

    String GET_BY_DATE = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` WHERE date = #{date}");

    String INSERT_DAY = ("INSERT INTO `mybatis-test`.`digital_currency_daily` " +
            "(`date`, `open`, `high`, `low`, `close`, `volume`,`marketcap`) " +
            "VALUES (#{date}, #{open}, #{high}, #{low}, #{close}, #{volume}, #{marketCap})");

//    @Select(GET_BY_DATE)
//    public Past30Days getByDate(String date);
//
//    @Insert(INSERT_DAY)
//    public int insertDay(Past30Days day);
}
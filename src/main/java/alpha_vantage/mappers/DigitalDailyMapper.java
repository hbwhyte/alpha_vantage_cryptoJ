package alpha_vantage.mappers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.external.DigitalDailyResponse;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DigitalDailyMapper {

    String GET_BY_DATE = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` WHERE date = #{date} AND isActive=1");

    String GET_BY_ID = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` WHERE id = #{id}");

    String INSERT_DAY = ("INSERT INTO `mybatis-test`.`digital_currency_daily` " +
            "(`date`, `symbol`, `open`, `high`, `low`, `close`, `volume`,`marketcap`) " +
            "VALUES (#{date}, #{symbol}, #{open}, #{high}, #{low}, #{close}, #{volume}, #{marketCap})");

    String DELETE_ENTRY = ("UPDATE `mybatis-test`.`digital_currency_daily` SET `isActive`=0 WHERE `id`= #{id}");

    String UPDATE_ENTRY = ("UPDATE `mybatis-test`.`digital_currency_daily` SET `date`=#{date}, `symbol`=#{symbol}, " +
            "`open`=#{open}, `high`=#{high}, `low`=#{low}, `close`=#{close}, `volume`=#{volume},`marketcap`=#{marketCap}, " +
            "`isActive`=#{isActive} " +
            "WHERE `id`= #{id}");

    String DOUBLE_CHECK = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` " +
            "WHERE `date`=#{arg0} AND `symbol`=#{arg1}");

    @Select(GET_BY_DATE)
    public DigitalCurrencyDaily getByDate(String date);

    @Select(GET_BY_ID)
    public DigitalCurrencyDaily getByID(int id);

    @Insert(INSERT_DAY)
    public int insertDay(DigitalCurrencyDaily day);

    @Delete(DELETE_ENTRY)
    public int deleteEntry(int id);

    @Update(UPDATE_ENTRY)
    void updateEntry(DigitalCurrencyDaily entry);

    @Select(DOUBLE_CHECK)
    public DigitalCurrencyDaily doubleCheck(String date, String symbol);

}
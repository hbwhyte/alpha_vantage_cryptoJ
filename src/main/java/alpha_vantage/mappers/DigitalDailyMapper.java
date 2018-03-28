package alpha_vantage.mappers;

import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.apache.ibatis.annotations.*;

@Mapper
public interface DigitalDailyMapper {

    String GET_BY_DATE = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` WHERE date = #{date}");

    String GET_BY_ID = ("SELECT * FROM `mybatis-test`.`digital_currency_daily` WHERE id = #{id}");

    String INSERT_DAY = ("INSERT INTO `mybatis-test`.`digital_currency_daily` " +
            "(`date`, `symbol`, `open`, `high`, `low`, `close`, `volume`,`marketcap`) " +
            "VALUES (#{date}, #{symbol}, #{open}, #{high}, #{low}, #{close}, #{volume}, #{marketCap})");

    //could modify delete statement to change a field "isActive" to 0 instead of deleting the field completely
    String DELETE_ENTRY = ("DELETE FROM `mybatis-test`.`digital_currency_daily` WHERE `id`= #{id}");

    String UPDATE_ENTRY = ("UPDATE `mybatis-test`.`digital_currency_daily` SET `date`=#{date}, `symbol`=#{symbol}, " +
            "`open`=#{open}, `high`=#{high}, `low`=#{low}, `close`=#{close}, `volume`=#{volume},`marketcap`=#{marketCap} " +
            "WHERE `id`= #{id}");


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

}
package alpha_vantage.mappers;

import alpha_vantage.enums.DigitalCurrency;
import alpha_vantage.model.internal.DigitalCurrencyDaily;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DigitalDailyMapper {

    String GET_BY_DATE = ("SELECT * FROM `digital_currency_daily` WHERE date = #{date} AND isActive=1");

    String GET_ALL_BY_SYMBOL = ("SELECT * FROM `digital_currency_daily` WHERE symbol = #{symbol} AND isActive=1");

    String GET_BY_ID = ("SELECT * FROM `digital_currency_daily` WHERE id = #{id}");

    String INSERT_DAY = ("INSERT INTO `digital_currency_daily` " +
            "(`date`, `symbol`, `open`, `high`, `low`, `close`, `volume`,`marketcap`) " +
            "VALUES (#{date}, #{symbol}, #{open}, #{high}, #{low}, #{close}, #{volume}, #{marketCap})");

    String DELETE_ENTRY = ("UPDATE `digital_currency_daily` SET `isActive`=0 WHERE `id`= #{id}");

    String UPDATE_ENTRY = ("UPDATE `digital_currency_daily` SET `date`=#{date}, `symbol`=#{symbol}, " +
            "`open`=#{open}, `high`=#{high}, `low`=#{low}, `close`=#{close}, `volume`=#{volume},`marketcap`=#{marketCap}, " +
            "`isActive`=#{isActive} " +
            "WHERE `id`= #{id}");

    String DOUBLE_CHECK = ("SELECT * FROM `digital_currency_daily` " +
            "WHERE `date`=#{arg0} AND `symbol`=#{arg1}");

    @Results(id = "dateResult", value = {
            @Result(property = "date", column = "date"),
            @Result(property = "symbol", column = "symbol"),
    })
    @Select(GET_BY_DATE)
    public DigitalCurrencyDaily getByDate(String date);

    @Results(id = "symbolResult", value = {
            @Result(property = "date", column = "date"),
            @Result(property = "symbol", column = "symbol"),
    })
    @Select(GET_ALL_BY_SYMBOL)
    public List<DigitalCurrencyDaily> getAllBySymbol(DigitalCurrency symbol);

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
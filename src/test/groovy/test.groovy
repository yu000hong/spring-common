import com.github.yu000hong.spring.common.util.JsonUtil

/**
 * Created by yuhong on 16/11/17.
 */

def i = 10
def d = 23.89
def n = null
def b = true

println(JsonUtil.fromJson(JsonUtil.toJson(i), Integer))
println(JsonUtil.fromJson(JsonUtil.toJson(d), Double))
println(JsonUtil.fromJson(JsonUtil.toJson(n), Object))
println(JsonUtil.fromJson(JsonUtil.toJson(b), Boolean))

println(d.getClass())
println(i.getClass())

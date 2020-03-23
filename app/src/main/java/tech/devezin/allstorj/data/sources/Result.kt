package tech.devezin.allstorj.data.sources

sealed class Result<Success, Failure> {
    abstract fun <T> map(mapping: (Success) -> T): Result<T, Failure>
    abstract fun <T> flatMap(mapping: (Success) -> Result<T, Failure>): Result<T, Failure>
    abstract fun <T> mapFailure(mapping: (Failure) -> T): Result<Success, T>
    abstract fun <T> flatMapFailure(mapping: (Failure) -> Result<Success, T>): Result<Success, T>
    abstract fun orElse(other: Success): Success
    abstract fun orElse(function: (Failure) -> Success): Success
    abstract fun <T> fold(success: (Success) -> T, failure: (Failure) -> T): T

    abstract suspend fun <T> suspendMap(mapping: suspend (Success) -> T): Result<T, Failure>
    abstract suspend fun <T> suspendFlatMap(mapping: suspend (Success) -> Result<T, Failure>): Result<T, Failure>
    abstract suspend fun <T> suspendMapFailure(mapping: suspend (Failure) -> T): Result<Success, T>
    abstract suspend fun <T> suspendFlatMapFailure(mapping: suspend (Failure) -> Result<Success, T>): Result<Success, T>
    abstract suspend fun suspendOrElse(function: suspend (Failure) -> Success): Success
}

data class Success<Success, Failure>(val successValue: Success) : Result<Success, Failure>() {
    override fun <T> map(mapping: (Success) -> T): Result<T, Failure> = Success(mapping(successValue))
    override fun <T> flatMap(mapping: (Success) -> Result<T, Failure>): Result<T, Failure> = mapping(successValue)
    override fun <T> mapFailure(mapping: (Failure) -> T): Result<Success, T> = Success(successValue)
    override fun <T> flatMapFailure(mapping: (Failure) -> Result<Success, T>): Result<Success, T> = Success(successValue)
    override fun orElse(other: Success): Success = successValue
    override fun orElse(function: (Failure) -> Success): Success = successValue
    override fun <T> fold(success: (Success) -> T, failure: (Failure) -> T): T = success(successValue)

    override suspend fun <T> suspendMap(mapping: suspend (Success) -> T): Result<T, Failure> = Success(mapping(successValue))
    override suspend fun <T> suspendFlatMap(mapping: suspend (Success) -> Result<T, Failure>): Result<T, Failure> = mapping(successValue)
    override suspend fun <T> suspendMapFailure(mapping: suspend (Failure) -> T): Result<Success, T> = Success(successValue)
    override suspend fun <T> suspendFlatMapFailure(mapping: suspend (Failure) -> Result<Success, T>): Result<Success, T> = Success(successValue)
    override suspend fun suspendOrElse(function: suspend (Failure) -> Success): Success = successValue
}

data class Error<Success, Failure>(val failureValue: Failure) : Result<Success, Failure>() {
    override fun <T> map(mapping: (Success) -> T): Result<T, Failure> = Error(failureValue)
    override fun <T> flatMap(mapping: (Success) -> Result<T, Failure>): Result<T, Failure> = Error(failureValue)
    override fun <T> mapFailure(mapping: (Failure) -> T): Result<Success, T> = Error(mapping(failureValue))
    override fun <T> flatMapFailure(mapping: (Failure) -> Result<Success, T>): Result<Success, T> = mapping(failureValue)
    override fun orElse(other: Success): Success = other
    override fun orElse(function: (Failure) -> Success): Success = function(failureValue)
    override fun <T> fold(success: (Success) -> T, failure: (Failure) -> T): T = failure(failureValue)

    override suspend fun <T> suspendMap(mapping: suspend (Success) -> T): Result<T, Failure> = Error(failureValue)
    override suspend fun <T> suspendFlatMap(mapping: suspend (Success) -> Result<T, Failure>): Result<T, Failure> = Error(failureValue)
    override suspend fun <T> suspendMapFailure(mapping: suspend (Failure) -> T): Result<Success, T> = Error(mapping(failureValue))
    override suspend fun <T> suspendFlatMapFailure(mapping: suspend (Failure) -> Result<Success, T>): Result<Success, T> = mapping(failureValue)
    override suspend fun suspendOrElse(function: suspend (Failure) -> Success): Success = function(failureValue)
}

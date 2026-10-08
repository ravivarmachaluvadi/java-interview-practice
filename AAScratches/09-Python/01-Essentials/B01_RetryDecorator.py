"""
=====================================================================
 Retry Decorator                             Python | Medium
=====================================================================

PROBLEM
  Write retry(times): a decorator that calls the function again whenever it raises, up to
  `times` calls in all, and raises the last error if every call failed. The decorated
  function keeps its own name and docstring.

EXAMPLE
  a function that fails twice and then works, under @retry(3)  ->  its result, after 3 calls
  a function that always fails, under @retry(2)                 ->  its error, after 2 calls
  @retry(3) def flaky(): ...                                    ->  flaky.__name__ == "flaky"

APPROACH  (a decorator factory: three functions inside each other)
  1. retry(times) returns decorator(fn).
  2. decorator returns wrapper(*args, **kwargs), marked with functools.wraps(fn).
  3. wrapper calls fn up to `times` times: return at the first success, keep the error
     otherwise.
  4. After the last try, raise the kept error.

KEY INSIGHT
  @retry(3) is two calls: retry(3) runs once, where the function is defined, and returns
  the real decorator, which then receives the function. Each inner function closes over the
  variables of the one around it, so wrapper still sees `times` and `fn` later.

COMPLEXITY
  Time  O(times) calls of fn
  Space O(1)

INTERVIEW FOLLOW-UPS
  - Wait between tries: time.sleep(delay * 2 ** attempt), exponential backoff.
  - Retry only some errors: an `on=(ConnectionError,)` argument and `except on as error`.
  - Without functools.wraps, help() and tracebacks show "wrapper" instead of the real name.
  - @retry with no brackets as well: check whether the first argument is callable.

RUN
  main() runs 4 cases and prints actual vs expected.
"""
import functools


def retry(times):
    """A decorator: call the function up to `times` times until it does not raise."""
    def decorator(fn):
        @functools.wraps(fn)
        def wrapper(*args, **kwargs):
            last = None
            for _ in range(times):
                try:
                    return fn(*args, **kwargs)
                except Exception as error:
                    last = error
            raise last
        return wrapper
    return decorator


def check(label, actual, expected):
    print(f"{label}: {actual}   expected {expected}")


def main():
    calls = []

    @retry(3)
    def flaky():
        """Fails twice, then works."""
        calls.append(1)
        if len(calls) < 3:
            raise ConnectionError("not yet")
        return "ok"

    check("case 1 fails twice, retry(3)", flaky(), "ok")
    check("case 2 calls made           ", len(calls), 3)

    @retry(2)
    def broken():
        raise ValueError("always")

    try:
        broken()
        outcome = "returned"
    except ValueError as error:
        outcome = f"raised {error}"
    check("case 3 always fails         ", outcome, "raised always")
    check("case 4 name kept            ", flaky.__name__, "flaky")


if __name__ == "__main__":
    main()
